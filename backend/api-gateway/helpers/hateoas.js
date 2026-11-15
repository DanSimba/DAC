//req = objeto de request do express

function getFirstHeaderValue(value) {
  return value?.split(',')[0].trim();
}

export function getBaseUrl(req, { baseUrl } = {}) {
  const configuredBaseUrl = baseUrl?.replace(/\/+$/, '');

  if (configuredBaseUrl) {
    return configuredBaseUrl;
  }

  const protocol = getFirstHeaderValue(req.get('x-forwarded-proto')) || req.protocol;
  const host = getFirstHeaderValue(req.get('x-forwarded-host')) || req.get('host');

  if (!protocol || !host) {
    throw new Error('Não foi possível determinar a URL pública do Gateway.');
  }

  return `${protocol}://${host}`;
}

export function createHref(req, path, options) {
  if (!path) {
    throw new Error('Caminho do link HATEOAS não informado.');
  }

  if (/^https?:\/\//i.test(path)) {
    return path;
  }

  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${getBaseUrl(req, options)}${normalizedPath}`;
}

export function createLinks(req, definitions, options) {
  return Object.fromEntries(
    Object.entries(definitions).map(([relation, definition]) => {
      if (typeof definition === 'string') {
        return [relation, { href: createHref(req, definition, options) }];
      }

      const { href, ...metadata } = definition;

      return [relation, {
        href: createHref(req, href, options),
        ...metadata
      }];
    })
  );
}

export function addLinks(req, resource, definitions, options) {
  return {
    ...resource,
    _links: createLinks(req, definitions, options)
  };
}
