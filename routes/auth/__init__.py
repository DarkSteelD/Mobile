import jwt

from fastapi import APIRouter
from database import *
from database.models import User, LoginUser, RegistrationUser

router = APIRouter(
    prefix="/auth",
    tags=["auth"]
)


@router.on_event("startup")
async def startup():
    """Создание таблицы пользователей"""
    database.create_table(
        "users",
        {
            "id": [DatabaseTypes.TEXT, DatabaseTypes.PRIMARY_KEY],
            "name": [DatabaseTypes.TEXT, DatabaseTypes.NOT_NULL],
            "surname": [DatabaseTypes.TEXT],
            "login": [DatabaseTypes.TEXT, DatabaseTypes.NOT_NULL, DatabaseTypes.UNIQUE],
            "password": [DatabaseTypes.TEXT, DatabaseTypes.NOT_NULL],
            "created_at": [DatabaseTypes.TEXT]
        }
    )


@router.get("login")
async def login(user: LoginUser):

    

    database_user = database.select_one(
        "users",
        DatabaseWhereQuery({
            DatabaseLogicalOperators.AND: {
                "login": user.login
            }
        })
    )


@router.get("registration")
async def registration():
    pass
