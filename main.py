from fastapi import FastAPI
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware

from routes import auth, news, delivery

app = FastAPI(
    title="Kotlin Projects Server",
    description="",
    version="0.0.1"
)

allow_all = ["*"]
app.add_middleware(
    CORSMiddleware,
    allow_origins=allow_all,
    allow_credentials=True,
    allow_methods=allow_all,
    allow_headers=allow_all
)

app.include_router(news.router)
app.include_router(auth.router)


@app.get("/")
async def root():
    return JSONResponse({
        "message": "Welcome to Kotlin Projects!"
    })
