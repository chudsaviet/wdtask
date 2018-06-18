import argparse
import io
import logging
import datetime
import socket
import time
import json
import psutil
import requests

logger = logging.getLogger('collector')
logging.basicConfig(
    format='[%(asctime)s] %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S',
    level=logging.INFO
)


def get_system_stats():
    cpu_times = psutil.cpu_times()
    virtual_memory = psutil.virtual_memory()
    disk = psutil.disk_usage('/')
    net_io_counters = psutil.net_io_counters()

    return {
        'cpu_user': cpu_times.user,
        'cpu_system': cpu_times.system,
        'cpu_idle': cpu_times.idle,
        'vmem_total': virtual_memory.total,
        'vmem_available': virtual_memory.available,
        'vmem_percent_free': virtual_memory.percent,
        'vmem_free': virtual_memory.free,
        'disk_root_total': disk.total,
        'disk_root_used': disk.used,
        'net_bytes_sent': net_io_counters.bytes_sent,
        'net_bytes_recv': net_io_counters.bytes_recv,
        'net_packets_recv': net_io_counters.packets_recv,
        'net_packets_sent': net_io_counters.packets_sent
    }


def generate_message():
    message = {
        'hostname': socket.gethostname(),
        'local_time': datetime.datetime.now().isoformat()
    }
    message.update(get_system_stats())
    return json.dumps(message)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--receive_url', type=str, required=True)
    parser.add_argument('--timeout', type=float, default=0.5)
    parser.add_argument('--debug', action='store_true')
    args = parser.parse_args()

    if args.debug:
        logger.setLevel(logging.DEBUG)

    logger.debug(f'Posting to {args.receive_url}')

    while True:
        message = generate_message()
        logger.debug(f'Posting message {message}')
        requests.post(args.receive_url, json=message)
        time.sleep(args.timeout)


if __name__ == '__main__':
    main()