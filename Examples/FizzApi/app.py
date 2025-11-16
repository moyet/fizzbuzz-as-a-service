import redis
import requests
import json
from functools import wraps

r = redis.Redis(host="127.0.0.1", port=6379, decode_responses=True)

hostname: str = "192.168.0.79"
portnumber: int = 1337


def redissed(r: redis.Redis, ttl: int = 180):
    def decorator(func):
        @wraps(func)
        def wrappper(*args, **kwargs):
            cache_key = f"{func.__name__}:{args[0]}"
            cached: str = r.get(cache_key)
            if cached:
                print(f"cached: {cached}")
                return cached

            result = func(*args, **kwargs)

            r.set(cache_key, result, ex=ttl)

            return result

        return wrappper

    return decorator


@redissed(r)
def fizzbuzz(num: int) -> str:
    res = requests.get(f"http://{hostname}:{portnumber}/fizzbuzz/{num}")
    data = json.loads(res.text)
    result: str = data["result"]

    return result


if __name__ == "__main__":
    for i in range(1, 101):
        print(fizzbuzz(i))
