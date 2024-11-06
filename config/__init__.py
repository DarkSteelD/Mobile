import toml
import os
import logging


class Configuration:
    def __init__(self):
        self.log = logging.getLogger('config')
        self.config_path = os.environ.get('CONFIG_PATH', os.path.join(
            os.getcwd(),
            "/config/config.toml"
        ))
        self.config = None

        self.read()

    def read(self):
        try:
            self.config = toml.load(self.config_path)
        except FileNotFoundError:
            self.log.error(f'Файл конфигурации {self.config_path} не найден')

    def __getitem__(self, item):
        try:
            if self.config:
                return self.config[item]
            return None
        except KeyError:
            self.log.error(f'Такого ключа {item} не существует')
            return None


config = Configuration()
