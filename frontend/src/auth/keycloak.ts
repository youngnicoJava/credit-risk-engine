import Keycloak from 'keycloak-js'

export const keycloak = new Keycloak({
  url: import.meta.env.VITE_OIDC_URL ?? 'http://localhost:8181',
  realm: import.meta.env.VITE_OIDC_REALM ?? 'credit-risk',
  clientId: import.meta.env.VITE_OIDC_CLIENT_ID ?? 'credit-risk-analyst',
})

let initialization: Promise<boolean> | undefined

export async function initializeAuth(): Promise<boolean> {
  initialization ??= keycloak.init({
    onLoad: 'check-sso',
    pkceMethod: 'S256',
    checkLoginIframe: false,
  })
  return initialization
}

export function userRoles(): string[] {
  return keycloak.realmAccess?.roles ?? []
}
