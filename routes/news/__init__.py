from fastapi import APIRouter
from fastapi.responses import JSONResponse

from database import *
from database.models import News, Channel, ClientNews, ClientChannel

router = APIRouter(
    prefix="/news",
    tags=["news"]
)


@router.on_event("startup")
async def startup():
    """Создание необходимых таблиц"""
    database.create_table(
        "news_channels",
        {
            "id": [DatabaseTypes.TEXT, DatabaseTypes.PRIMARY_KEY],
            "name": [DatabaseTypes.TEXT, DatabaseTypes.NOT_NULL, DatabaseTypes.UNIQUE],
            "created_at": [DatabaseTypes.TEXT]
        }
    )

    database.create_table(
        "channel_articles",
        {
            "id": [DatabaseTypes.TEXT, DatabaseTypes.PRIMARY_KEY],
            "title": [DatabaseTypes.TEXT, DatabaseTypes.NOT_NULL, DatabaseTypes.UNIQUE],
            "date": [DatabaseTypes.TEXT, DatabaseTypes.NOT_NULL],
            "content": [DatabaseTypes.TEXT, DatabaseTypes.NOT_NULL],
            "created_at": [DatabaseTypes.TEXT],
            "channel_id": [DatabaseTypes.TEXT, DatabaseTypes.NOT_NULL]
        },
        {
            "channel_id": DatabaseReference(
                "news_channels",
                "id",
                on_delete=DatabaseActions.CASCADE
            )
        }
    )


@router.get("/")
async def get_channels():
    channels = database.select_many(
        "news_channels",
        ["*"]
    )

    return JSONResponse(channels)


@router.post("/")
async def create_channel(client_channel: ClientChannel):
    """Создание необходимых таблиц"""
    client_channel_dict = client_channel.model_dump()
    channel_model = Channel(
        **client_channel_dict
    )

    database.insert("news_channels", channel_model.model_dump())


@router.get("/{channel_id}")
async def get_channel_news(channel_id: str):
    channel = database.select_many(
        "channel_articles",
        ['id', 'title', 'date'],
        DatabaseWhereQuery({
            DatabaseLogicalOperators.AND.value: {
                "channel_id": channel_id
            }
        })
    )

    return JSONResponse(channel)


@router.post("/{channel_id}")
async def create_news(channel_id: str, client_news: ClientNews):
    client_news_dict = client_news.model_dump()
    news_model = News(
        **client_news_dict
    )

    database.insert("channel_articles", news_model.model_dump())


@router.get("/{channel_id}/{news_id}")
async def get_news_info(channel_id: str, news_id: str):
    channel = database.select_many(
        "channel_articles",
        ["*"],
        DatabaseWhereQuery({
            DatabaseLogicalOperators.AND.value: {
                "channel_id": channel_id,
                "id": news_id
            }
        })
    )

    return JSONResponse(channel)
