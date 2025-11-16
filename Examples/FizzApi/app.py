from functools import wraps
import os
import logging
import json
import redis
import requests
from typing import Any

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

REDIS_HOST = os.getenv("REDIS_HOST", "127.0.0.1")
REDIS_PORT = int(os.getenv("REDIS_PORT", "6379"))
REDIS_TTL = int(os.getenv("REDIS_TTL", "180"))

API_HOST = os.getenv("FIZZ_API_HOST", "192.168.0.79")
API_PORT = int(os.getenv("FIZZ_API_PORT", "1337"))

r = redis.Redis(host=REDIS_HOST, port=REDIS_PORT, decode_responses=True)

def redis_cached(redis_client: redis.Redis, ttl: int = REDIS_TTL):
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            try:
                # Build a deterministic cache key from function name + args + kwargs
                key_body = {
                    "args": args,
                    "kwargs": kwargs,
                }
                cache_key = f"{func.__name__}:{json.dumps(key_body, default=repr, sort_keys=True)}"
            except Exception:
                cache_key = f"{func.__name__}:{repr((args, kwargs))}"

            try:
                cached = redis_client.get(cache_key)
            except Exception as e:
                logger.warning("Redis GET failed: %s", e)
                cached = None

            if cached is not None:
                logger.debug("cache hit for %s", cache_key)
                return cached

            result = func(*args, **kwargs)

            try:
                # Store as JSON string if possible, otherwise str()
                to_store = result if isinstance(result, str) else json.dumps(result, default=repr)
                redis_client.set(cache_key, to_store, ex=ttl)
            except Exception as e:
                logger.warning("Redis SET failed: %s", e)

            return result

        return wrapper

    return decorator

@redis_cached(r)
def fizzbuzz(num: int) -> str:
    url = f"http://{API_HOST}:{API_PORT}/fizzbuzz/{num}"
    try:
        res = requests.get(url, timeout=5)
        res.raise_for_status()
        data = res.json()
        return data.get("result", "")
    except requests.RequestException as e:
        logger.error("Request to %s failed: %s", url, e)
        raise

if __name__ == "__main__":
    for i in range(1, 101):
        try:
            print(fizzbuzz(i))
        except Exception as e:
            logger.error("Failed to get fizzbuzz for %d: %s", i, e)