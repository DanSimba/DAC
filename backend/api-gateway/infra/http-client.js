export class HttpError extends Error {
  constructor(status, message, body) {
    super(message);
    this.name = 'HttpError';
    this.status = status;
    this.body = body;
  }
}

export async function request(
  url,
  {
    method = 'GET',
    headers = {},
    body,
    unavailableMessage = 'Serviço indisponível.'
  } = {}
) {
  let response;

  try {
    response = await fetch(url, {
      method,
      headers,
      body
    });
  } catch {
    throw new HttpError(503, unavailableMessage);
  }

  let text;

  try {
    text = await response.text();
  } catch {
    throw new HttpError(503, unavailableMessage);
  }

  let data;

  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  if (!response.ok) {
    const message = data?.message ??
      data?.mensagem ??
      (typeof data === 'string' ? data : unavailableMessage);

    throw new HttpError(response.status, message, data);
  }

  return {
    status: response.status,
    data,
    headers: response.headers
  };
}
