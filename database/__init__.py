import logging
import sqlite3 as sql

from typing import Optional, Dict, List
from enum import Enum


class DatabaseTypes(Enum):
    PRIMARY_KEY = "PRIMARY KEY"
    FOREIGN_KEY = "FOREIGN KEY"
    INTEGER = "INTEGER"
    NULL = "NULL"
    NOT_NULL = "NOT NULL"
    REAL = "REAL"
    BLOB = "BLOB"
    TEXT = "TEXT"
    UNIQUE = "UNIQUE"


class DatabaseActions(Enum):
    CASCADE = "CASCADE"
    SET_NULL = "SET NULL"
    RESTRICT = "RESTRICT"
    NO_ACTION = "NO ACTION"
    SET_DEFAULT = "SET DEFAULT"
    IGNORE = "IGNORE"
    EXISTS = "EXISTS"
    NOT = "NOT"


class DatabaseLogicalOperators(Enum):
    AND = "AND"
    OR = "OR"


class DatabaseWhereQuery:
    def __init__(self, query: Dict):
        self.and_query = query.get(DatabaseLogicalOperators.AND.value, {})
        self.or_query = query.get(DatabaseLogicalOperators.OR.value, {})

    @property
    def and_operator(self):
        return f"""({f' {DatabaseLogicalOperators.AND.value} '.join(
            list(
                map(
                    lambda key: f'{key}="{self.and_query[key]}"',
                    self.and_query
                )
            )
        )})"""

    @property
    def or_operator(self):
        return f"""
            ({f' {DatabaseLogicalOperators.OR.value} '.join(
            list(
                map(
                    lambda key: f'{self.or_query[key]}',
                    self.or_query
                )
            )
        )})
            """


class DatabaseReference:
    def __init__(
            self,
            table_name: str,
            table_col: str,
            on_delete: Optional[DatabaseActions] = None,
            on_update: Optional[DatabaseActions] = None
    ):
        self.table = table_name
        self.column = table_col
        self._on_delete = on_delete
        self._on_update = on_update

    @property
    def on_update(self) -> str:
        if not self._on_update:
            return ""
        return f" ON UPDATE {self._on_update.value}"

    @property
    def on_delete(self) -> str:
        if not self._on_delete:
            return ""
        return f" ON DELETE {self._on_delete.value}"


class Database:
    def __init__(self, db_name: str):
        self.log = logging.getLogger("database")
        self.connection = sql.connect(db_name)
        self.connection.row_factory = self.dict_factory
        self.cursor = self.connection.cursor()

    def execute(self, command: str):
        try:
            self.cursor.execute(command)
        except sql.Error as err:
            self.log.error(f"[EXECUTE]:{err}")

    def _select(self, table_name: str, cols: list, query: Optional[Dict], order_by: Optional[str]):
        try:
            command = self.select_command(table_name, cols, query, order_by)
            print(f"command = {command}")
            result = self.cursor.execute(command)
            print(f"result = {result}")
            return result
        except sql.Error as err:
            self.log.error(f"[SELECT]:{err}")

    def select_one(
            self,
            table_name: str,
            cols: list,
            query: Optional[DatabaseWhereQuery] = None,
            order_by: Optional[str] = None
    ):
        """Выбор одного значения из таблицы"""
        try:
            result = self._select(table_name, cols, query, order_by)
            return result.fetchone()
        except sql.Error as err:
            self.log.error(f"[SELECT_ONE]:{err}")

    def select_many(
            self,
            table_name: str,
            cols: list,
            query: Optional[DatabaseWhereQuery] = None,
            order_by: Optional[str] = None
    ):
        """Выбор нескольких значений из таблицы"""
        try:
            result = self._select(table_name, cols, query, order_by)
            return result.fetchall()
        except (AttributeError, sql.Error) as err:
            self.log.error(f"[SELECT_MANY]:{err}")

    def update(self, table_name: str, cols: list, query: Dict):
        """Обновляет занчения в колонках для таблицы в базе"""
        try:
            command = self.update_command(table_name, cols, query)
            self.cursor.execute(command, (*query.values(),))
            self.connection.commit()
        except sql.Error as err:
            self.log.error(f"[UPDATE]:{err}")

    def create_table(
            self,
            table_name: str,
            table_cols: Dict[str, List[DatabaseTypes]],
            foreign_keys: Optional[Dict[str, DatabaseReference]] = None):
        """Создает таблицу внутри базы данных"""
        try:
            command = self.create_command(table_name, table_cols, foreign_keys)
            print(command)
            self.cursor.execute(command)
            self.connection.commit()
        except sql.Error as err:
            self.log.error(f"[CREATE]:{err}")

    def delete(self, table_name, query: Dict):
        """Удаление данных из таблицы по запросу"""
        try:
            command = self.delete_command(table_name, query)
            self.cursor.execute(command, (*query.values(),))
            self.connection.commit()
        except sql.Error as err:
            self.log.error(f"[DELETE]:{err}")

    def insert(self, table_name: str, cols: Dict):
        """Вставка данных в таблицу по колонам"""
        try:
            command = self.insert_command(table_name, list(cols.keys()))
            print(command)
            print(*cols.values())
            self.cursor.execute(command, (*cols.values(),))
            self.connection.commit()
        except sql.Error as err:
            self.log.error(f"[INSERT]:{err}")

    def check_table_exists(self, table_name):
        """Проверка существования таблицы с именем {table_name}"""
        table = self.select_one(
            "sqlite_master",
            ['name'],
            query=DatabaseWhereQuery({
                DatabaseLogicalOperators.AND.value: {
                    'type': 'table',
                    'name': table_name
                }
            })
        )

        print(table)
        return table

    @staticmethod
    def dict_factory(cursor, row):
        obj = {}
        for idx, column in enumerate(cursor.description):
            obj[column[0]] = row[idx]

        return obj

    @staticmethod
    def update_command(table_name: str, cols_to_upd: List[str], query: Dict):

        cols_string = ','.join(list(map(lambda col: f"{col} = ?", cols_to_upd)))
        query_keys_string = ','.join(list(map(lambda key: f"{key} = ?", query)))

        command_string = f"""
        UPDATE {table_name} SET ({cols_string}) WHERE ({query_keys_string})
        """

        return command_string

    @staticmethod
    def delete_command(table_name: str, query: Dict):

        query_keys_string = ','.join(list(map(lambda key: f"{key} = ?", query)))

        command_string = f"""
        DELETE FROM {table_name} WHERE {query_keys_string}
        """

        return command_string

    @staticmethod
    def insert_command(table_name: str, table_cols: List[str]):

        values = list(map(lambda item: '?', table_cols))

        command_string = f"""
        INSERT INTO {table_name} ({', '.join(table_cols)}) VALUES ({', '.join(values)})
        """

        return command_string

    @staticmethod
    def create_command(
            table_name: str,
            table_cols: Dict[str, List[DatabaseTypes]],
            foreign_keys: Optional[Dict[str, DatabaseReference]]
    ):

        columns = []
        for col_key, col_type in table_cols.items():
            col_type_string = ' '.join(list(map(
                lambda col_item: col_item.value,
                col_type
            )))

            columns.append(
                f"{col_key} {col_type_string}"
            )
        if foreign_keys:
            for key, reference in foreign_keys.items():
                reference_string = f"""
                FOREIGN KEY ({key}) REFERENCES {reference.table} ({reference.column})
                {reference.on_delete}{reference.on_update}"""

                columns.append(reference_string)

        command_string = f"""
        CREATE TABLE IF NOT EXISTS {table_name} (
            {','.join(columns)}
        )
        """

        return command_string

    @staticmethod
    def select_command(table_name: str, cols: list, query: Optional[DatabaseWhereQuery], order_by: Optional[str]):
        selected_cols = ", ".join(cols)
        command_string = f"""SELECT {selected_cols} FROM {table_name}"""

        if query:
            command_string += f" WHERE {query.and_operator}"

        if order_by:
            command_string += f" ORDER BY {order_by}"

        return command_string


database = Database("community.db")
