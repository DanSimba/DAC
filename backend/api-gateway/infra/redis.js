import { createClient } from 'redis';

const CONNECTION_ATTEMPTS = 10;
const RETRY_DELAY = 3000;

export class Redis {
  #client;
  #connectionPromise;

  async initialize() {
    return this.getClient();
  }

  async getClient() {
    if (this.#client?.isReady || this.#client?.isOpen) {
      return this.#client;
    }

    if (this.#connectionPromise) {
      return this.#connectionPromise;
    }

    const url = process.env.REDIS_URL;

    if (!url) {
      throw new Error('REDIS_URL não informada.');
    }

    if (!this.#client) {
      this.#client = createClient({
        url,
        socket: {
          reconnectStrategy: retries => {
            if (retries >= CONNECTION_ATTEMPTS) {
              return false;
            }

            return RETRY_DELAY;
          }
        }
      });

      this.#client.on('error', error => {
        console.error('Erro no Redis:', error);
      });
    }

    this.#connectionPromise = this.#client.connect()
      .then(() => this.#client)
      .finally(() => {
        this.#connectionPromise = undefined;
      });

    return this.#connectionPromise;
  }

  async close() {
    if (this.#connectionPromise) {
      await this.#connectionPromise.catch(() => undefined);
    }

    if (this.#client?.isOpen) {
      await this.#client.quit().catch(() => {
        this.#client.destroy();
      });
    }

    this.#client = undefined;
    this.#connectionPromise = undefined;
  }
}

export const redis = new Redis();
