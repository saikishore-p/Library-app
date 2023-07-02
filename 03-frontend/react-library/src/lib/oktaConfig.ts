export const oktaConfig = {
    clientId: '0oa8svcmjvYf4lemX5d7',
    issuer: 'https://dev-68551943.okta.com/oauth2/default',
    redirectUri: 'https://localhost:3000/login/callback',
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsCheck: true
}