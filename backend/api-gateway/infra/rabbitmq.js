import amqp from 'amqplib';

const CONNECTION_ATTEMPTS = 10;
const RETRY_DELAY = 3000;

export class RabbitMQ {
  #connection;
  #channel;
  #connectionPromise;
  #closing = false;

  async initialize() {
    this.#closing = false;
    return this.getChannel();
  }

  async getChannel() {
    if (this.#channel) {
      return this.#channel;
    }

    if (this.#connectionPromise) {
      return this.#connectionPromise;
    }

    this.#connectionPromise = this.#connectWithRetry().finally(() => {
      this.#connectionPromise = undefined;
    });

    return this.#connectionPromise;
  }

  async close() {
    this.#closing = true;

    if (this.#connectionPromise) {
      await this.#connectionPromise.catch(() => undefined);
    }

    const channel = this.#channel;
    const connection = this.#connection;

    this.#channel = undefined;
    this.#connection = undefined;

    await channel?.close().catch(() => undefined);
    await connection?.close().catch(() => undefined);
  }

  async #connectWithRetry() {
    const url = process.env.RABBITMQ_URL;

    if (!url) {
      throw new Error('RABBITMQ_URL não informada.');
    }

    let lastError;

    for (let attempt = 1; attempt <= CONNECTION_ATTEMPTS; attempt += 1) {
      try {
        return await this.#connect(url);
      } catch (error) {
        lastError = error;

        if (attempt < CONNECTION_ATTEMPTS) {
          console.warn(
            `RabbitMQ indisponível. Tentativa ${attempt}/${CONNECTION_ATTEMPTS}.`
          );

          await new Promise(resolve => setTimeout(resolve, RETRY_DELAY));
        }
      }
    }

    throw lastError;
  }

  async #connect(url) {
    const connection = await amqp.connect(url);

    try {
      const channel = await connection.createChannel();

      connection.on('error', error => {
        console.error('Erro na conexão com RabbitMQ:', error);
      });

      connection.on('close', () => {
        this.#handleDisconnect(connection, channel);
      });

      channel.on('error', error => {
        console.error('Erro no canal do RabbitMQ:', error);
      });

      channel.on('close', () => {
        this.#handleDisconnect(connection, channel);
      });

      this.#connection = connection;
      this.#channel = channel;

      return channel;
    } catch (error) {
      await connection.close().catch(() => undefined);
      throw error;
    }
  }

  #handleDisconnect(connection, channel) {
    if (this.#connection !== connection && this.#channel !== channel) {
      return;
    }

    this.#connection = undefined;
    this.#channel = undefined;

    if (!this.#closing) {
      connection.close().catch(() => undefined);
      this.getChannel().catch(error => {
        console.error('Não foi possível reconectar ao RabbitMQ:', error);
      });
    }
  }
}

export const rabbitMQ = new RabbitMQ();
