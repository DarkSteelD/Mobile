import hashlib

from config import config


HASH_KEY = config['keys']['SHA256_KEY']


def hash_password(password: str) -> str:
    """Хэширование пароля используя алгоритм SHA256"""
    password_bytes = password.encode("utf-8")
    salt_bytes = HASH_KEY.encode("utf-8")

    hash_function = hashlib.sha256()
    hash_function.update(salt_bytes + password_bytes)

    hashed_password = hash_function.hexdigest()
    return hashed_password


def encode_jwt_token() -> str:
    pass


def decode_jwt_token() -> str:
    pass


