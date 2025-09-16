�� ���ڷ�������ǰ��Ŀ�������������

�����Ŀ (flowable-demo)

- ����ջ: Java 17 + Spring Boot 3.1.5 + Spring Security + OAuth2 + MySQL + MyBatis Plus
- �˿�: 8080 (application.yml:2)
- ��Ҫ����: OAuth2 ��Ȩ����������Դ������
- ��������:
    - OAuth2 Authorization Server
    - OAuth2 Resource Server
    - Spring Security
    - MySQL + MyBatis Plus
    - Thymeleaf ģ������

ǰ����Ŀ (flowable-demo-ui)

- ����ջ: �� HTML/CSS/JavaScript + Node.js ��̬������
- �˿�: 3000 (server.js:50)
- ��Ҫ����: OAuth2 �ͻ�����ʾ����
- �ص�:
    - �򵥵ľ�̬�ļ�����
    - ֧�� SPA ·�� (server.js:13-14)
    - OAuth2 ��Ȩ������ʾ

��Ŀ�ܹ�

����һ�������� OAuth2 ��ʾϵͳ��������
- ��Ȩ������ (���): ���� OAuth2 ��Ȩ�� JWT token ����
- �ͻ���Ӧ�� (ǰ��): ��ʾ OAuth2 ��Ȩ�����̺� API ����
- ���ݿ�: MySQL �洢�û��Ϳͻ�����Ϣ

> �ҬF��߀�Ǜ]�и�����oauth2��ԭ��
��Ҫ���^����һ�飬
�������Ŀ��resource/doc/oauth2.md �ļ�;
����һЩ�Ɇ���
��һ�� ��ʲ�N���� ���հl�F�A�Σ� �@���A������ʲ�N��

�� Read(flowable-demo\src\main\resources\doc\oauth2.md)
? ?Read 221 lines (ctrl+o to expand)

�� �ļ�������ʾ�б������⣬���Ҽ���Ƿ�����������ĵ���

�� Bash(dir "flowable-demo\src\main\resources\doc")
? ?authorizationServer.md            oauth2_client_register_step.m
flow                              oauth2_client_register_step1.md
front_step1.md                    oauth2_client_register_step2.md
�� +3 lines (ctrl+o to expand)

�� Read(flowable-demo\src\main\resources\doc\oauth2_principle.md)
? ?Read 17 lines (ctrl+o to expand)

�� ��Щ�ĵ�Ҳ�б������⡣���Ҳ鿴��Ŀ¼�µ��ĵ���

�� Read(README-OAUTH2.md)
? ?Read 195 lines (ctrl+o to expand)

�� Read(2025-09-04-oauth2.md)
? ?Read 50 lines (ctrl+o to expand)

�� �����ĵ����ݣ������ش������OAuth2�з����ֽ׶ε����ʣ�

OAuth2�еķ����ֽ׶�

�����ֽ׶Σ�Service Discovery����OAuth2����ͼ�ж�Ӧ��һ����GET /.well-known/oauth-authorization-server

Ϊʲô��Ҫ�����֣�

1. ��׼���˵㷢��
   - OAuth2�����˶���˵㣨authorize��token��userinfo�ȣ�
   - ��ͬ����Ȩ����������ʹ�ò�ͬ��·��
   - �ͻ�����Ҫ֪����Щ�˵��ȷ��λ��
2. ��̬����
   - ����ͻ���Ӳ����˵�URL
   - ֧����Ȩ������Ǩ�ƻ����ñ��
   - �ṩ���Ĳ���ܹ�

�����ֽ׶���ʲô��

����: GET /.well-known/oauth-authorization-server

��Ӧ���ݰ�����
{
"authorization_endpoint": "http://localhost:8080/oauth2/authorize",
"token_endpoint": "http://localhost:8080/oauth2/token",
"userinfo_endpoint": "http://localhost:8080/oauth2/userinfo",
"jwks_uri": "http://localhost:8080/oauth2/jwks",
"introspection_endpoint": "http://localhost:8080/oauth2/introspect",
"supported_grant_types": ["authorization_code", "refresh_token"],
"supported_scopes": ["openid", "profile", "read", "write"]
}

ʵ������

1. ���߿ͻ���"�����ṩʲô����"��
   - ֧����Щ��Ȩ����
   - �ṩ��Щ�˵�
   - ֧����Щscope��Χ
2. ʵ�ַ�����
   - �ͻ��˲���ҪӲ����URL
   - ֧�ֶ໷�����𣨿���/����/������
   - ���ڷ������˸�������
3. ��ѭOAuth2�淶��
   - RFC 8414����ı�׼���ֻ���
   - ȷ����������

����⣺������ȥһ�����̵꣬�ȿ��ſڵ�"����ָ��"�˽���ҵ��ṩʲô�����������ṩ��Ȼ���پ��幺����Ʒ��

> �@��Ո���ǿ͑������ڙ���������Ӱl���Ո��᣿ԃ���ڙ�������@�Y����Щ�ڙ�Ľӿڣ�

�� �ǵģ���������ȫ��ȷ��

����������ȷʵ�ǿͻ�������Ȩ�������������������Ŀ�ľ���ѯ�ʣ�"�������ṩ��ЩOAuth2����ͽӿڣ�"

�������̣�

1. �ͻ�����������

// ǰ�˿ͻ��˴���ʾ��
const response = await fetch('http://localhost:8080/.well-known/oauth-authorization-server');
const config = await response.json();

2. ��Ȩ��������Ӧ

{
"authorization_endpoint": "http://localhost:8080/oauth2/authorize",
"token_endpoint": "http://localhost:8080/oauth2/token",
"userinfo_endpoint": "http://localhost:8080/oauth2/userinfo",
// ... ����˵���Ϣ
}

3. �ͻ��˻��"����˵�"

���ڿͻ���֪���ˣ�
- Ҫ��ȡ��Ȩ�룬ȥ��/oauth2/authorize
- Ҫ����token��ȥ��/oauth2/token
- Ҫ��ȡ�û���Ϣ��ȥ��/oauth2/userinfo

ΪʲôҪ������ƣ�

1. ����Ӳ���룺�ͻ��˲���Ҫ��ǰ֪������URL
2. ��׼��������OAuth2����������ͬһλ���ṩ���"����˵�"
3. ����ԣ���Ȩ�����������������ö˵�·��

��ȣ������㵽һ���µĹ������ģ��ȿ�¥��ָ��ͼ�˽�"��װ��2¥��������3¥����ӰԺ��4¥"��Ȼ����ȥ�����¥�㹺�

��������OAuth2�ͻ��˾߱���"��������"��������

> http://localhost:8080/.well-known/oauth-authorization-server
��ȥ�����Ͷ˿��⣬url�ϵ����������ǹ̶��Ć᣿ ߀���f���ڙ�������Զ��x���f������
��һ����.well-known/oauth-authorization-server��
a/b/c-server Ҳ����

�� URL·���Ǳ�׼�̶��ģ������Զ���ģ�

��׼�淶Ҫ��

���� RFC 8414 (OAuth 2.0 Authorization Server Metadata) �淶��

/.well-known/oauth-authorization-server

���·����ǿ�ƹ̶��ģ�������ѭOAuth2��׼����Ȩ�����������������·���ṩԪ���ݡ�

Ϊʲô����̶���

1. �������ԣ��ͻ���֪��ȥ������"����˵�"
2. ��׼����ȫ������OAuth2����������ͬ����"���ƺ�"
3. �Զ����֣����ߺͿ�����Զ�����

��ʵ����

- Google: https://accounts.google.com/.well-known/oauth-authorization-server
- Microsoft: https://login.microsoftonline.com/common/.well-known/oauth-authorization-server
- GitHub: https://github.com/.well-known/oauth-authorization-server

�����Զ���Ϊ /a/b/c-server

����ĳ��Զ���·����
- ? Υ��RFC��׼
- ? �ͻ����޷��Զ�����
- ? �����������޷�ʶ��
- ? ʧȥ��׼������

������

����������վ�Ļ�����Э�鶼������� /robots.txt�����ܷ��� /my-robots.txt һ��������Э��Լ�������Ǹ���ϲ�ã�

�ܽ᣺.well-known/oauth-authorization-server ��OAuth2��"���ʱ�׼���ƺ�"���������ء�

> ���ҿ�����⠑�������Ҫ�_�lһ���������͑��˽���google����wechat��
�������@���f����ֻ�������Ͷ˿ڿ��ܲ�һ�£������Ķ���һ�ӵĆ�

�� ����ȫ��ȷ����Ȼ·���̶�����˵��������ݻ��кܴ���졣

·���Ǳ�׼��

Google:  https://accounts.google.com/.well-known/oauth-authorization-server
WeChat:  https://api.weixin.qq.com/.well-known/oauth-authorization-server
GitHub:  https://github.com/.well-known/oauth-authorization-server

��˵�������ݲ���ܴ�

Google��˵������ܰ�����

{
"authorization_endpoint": "https://accounts.google.com/oauth2/auth",
"token_endpoint": "https://oauth2.googleapis.com/token",
"userinfo_endpoint": "https://openidconnect.googleapis.com/v1/userinfo",
"supported_scopes": ["openid", "email", "profile", "drive.readonly"],
"supported_grant_types": ["authorization_code", "refresh_token", "client_credentials"]
}

΢�ŵ�˵������ܰ�����

{
"authorization_endpoint": "https://open.weixin.qq.com/connect/oauth2/authorize",
"token_endpoint": "https://api.weixin.qq.com/sns/oauth2/access_token",
"userinfo_endpoint": "https://api.weixin.qq.com/sns/userinfo",
"supported_scopes": ["snsapi_base", "snsapi_userinfo"],
"supported_grant_types": ["authorization_code"]
}

��Ҫ����

1. �˵�URL·����ͬ�������Զ���
2. ֧�ֵ�scope��ͬ��Google��drive���ʣ�΢����С����Ȩ��
3. ֧�ֵĹ��ܲ�ͬ����Щ֧��PKCE����Щ��֧��
4. ���������ͬ�����ҿ�����������չ

�����������ͻ��˵Ĳ���

// ��̬��ȡ����
const config = await fetch(`${providerDomain}/.well-known/oauth-authorization-server`);
const metadata = await config.json();

// ����ʵ�ʷ��صĶ˵����OAuth����
window.location.href = `${metadata.authorization_endpoint}?client_id=...`;

�ܽ᣺���ƺ�ͳһ����ÿ�ҵ����Ķ����ͷ���ʽ����һ����

> �_�l�߽o���ڙ෽�б�ĕr�����磺����google��wechat��facebook;
����c�􌦑����ڙ෽��Ԓ���@Щ�B�����Ă���ַ���u�����D���������ڙ෽���@���B�����ڙ෽�ṩ�Ć᣿
߀���fȥ�������ԵĹپW��

�� ������ӵ�ַ����Ȩ���ṩ�ģ�������ȥ��������ҵģ�

��������Ҫ���Ĳ���

1. ����Ȩ��ע��Ӧ��

ÿ����Ȩ������ר�ŵĿ�����ƽ̨��

- Google: https://console.cloud.google.com/
- ΢��: https://open.weixin.qq.com/
- Facebook: https://developers.facebook.com/
- GitHub: https://github.com/settings/developers

2. ��ȡ�ٷ��ṩ����ȨURL

ע�����Ȩ�����ṩ��׼����Ȩ�˵㣺

const providers = {
google: {
authUrl: 'https://accounts.google.com/oauth2/auth',
clientId: 'your-google-client-id'
},
wechat: {
authUrl: 'https://open.weixin.qq.com/connect/oauth2/authorize',
clientId: 'your-wechat-appid'
},
facebook: {
authUrl: 'https://www.facebook.com/v18.0/dialog/oauth',
clientId: 'your-facebook-app-id'
}
};

3. �û����ʱ��ת

function loginWith(provider) {
const config = providers[provider];
const params = new URLSearchParams({
client_id: config.clientId,
redirect_uri: 'http://localhost:3000/callback',
response_type: 'code',
scope: 'openid profile email'
});

    // ��ת����Ȩ���Ĺٷ���Ȩҳ��
    window.location.href = `${config.authUrl}?${params}`;
}

��Ҫ˵��

1. URL�ǹٷ���׼�ģ���������ҵģ���OAuth2�淶�����
2. ��ҪԤ��ע�᣺�����ڸ�ƽ̨ע��Ӧ�û��client_id
3. ����ͨ�������ֻ�ȡ��/.well-known/oauth-authorization-server
4. ÿ�Ҹ�ʽ�������в�ͬ�������Ĳ�������

�ܽ᣺��ȨURL�Ǹ���ƽ̨�ڿ������ĵ�����ȷ�ṩ�ı�׼�˵㣬�����Լ��²������ҵģ�