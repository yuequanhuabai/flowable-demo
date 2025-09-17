���ڵ�ǰĿ¼����һ�µ�ǰ��Ŀ����ǰ�˺ͺ��

�� ��������һ�µ�ǰ��Ŀ�Ľṹ����ɡ�

�� Bash(ls -la)
? ?total 128
drwxr-xr-x 1 baiyingwaming 197121     0  9�� 15 09:39 .
drwxr-xr-x 1 baiyingwaming 197121     0  9�� 11 10:13 ..
�� +8 lines (ctrl+o to expand)

�� Bash(ls -la flowable-demo)
? ?total 58
drwxr-xr-x 1 baiyingwaming 197121     0  9�� 17 11:37 .
drwxr-xr-x 1 baiyingwaming 197121     0  9�� 15 09:39 ..
�� +11 lines (ctrl+o to expand)

�� Bash(ls -la flowable-demo-ui)
? ?total 75
drwxr-xr-x 1 baiyingwaming 197121    0  9�� 15 09:14 .
drwxr-xr-x 1 baiyingwaming 197121    0  9�� 15 09:39 ..
�� +11 lines (ctrl+o to expand)

�� Read(flowable-demo\pom.xml)
? ?Read 166 lines (ctrl+o to expand)

�� Read(flowable-demo-ui\package.json)
? ?Read 13 lines (ctrl+o to expand)

�� Bash(find flowable-demo/src -name "*.java" | head -10)
? ?flowable-demo/src/main/java/com/example/flowabledemo/config/DatabaseInitializer.java
flowable-demo/src/main/java/com/example/flowabledemo/config/OAuth2AuthorizationServerConfig.java
flowable-demo/src/main/java/com/example/flowabledemo/config/PasswordEncoderConfig.java
�� +7 lines (ctrl+o to expand)

�� Read(flowable-demo-ui\index.html)
? ?Read 30 lines (ctrl+o to expand)

�� ��Ŀ����

����һ��OAuth2 & OIDC�����֤���������Ŀ������ǰ��˷���ļܹ���

��� (flowable-demo)

����ջ��
- Spring Boot 3.1.5 + Java 17
- Spring Security 6 + Spring Authorization Server
- MySQL 8.0 + MyBatis Plus
- OAuth2 ��Ȩ����������Դ������ʵ��

��Ҫ���ܣ�
- OAuth2��Ȩ������ (OAuth2AuthorizationServerConfig.java)
- �û���֤����Ȩ������ (AuthController.java, ConsentController.java)
- OAuth2�ͻ��˹��� (OAuth2ClientController.java)
- ��ȫ���� (SecurityConfig.java)
- ���ݿ��ʼ�� (DatabaseInitializer.java)

ǰ�� (flowable-demo-ui)

����ջ��
- ��HTML5 + ԭ��JavaScript
- Node.js��̬������
- CSS��ʽ�ļ�

��Ҫ���ܣ�
- OAuth2��֤������ʾ����
- �û���¼/�ǳ�����
- �ܱ���API����
- ��Ȩ���̿��ӻ�

��Ŀ�ص�

1. רע�����֤����Ȼ���ư���"flowable"����ʵ��רע��OAuth2/OIDC��֤
2. �������������ͬʱ�ṩOAuth2��OIDC������֤����
3. �ĵ���ȫ��������ϸ��OAuth2�ĵ��Ϳͻ���ע��˵��
4. ��ʾ�Ѻã�ǰ���ṩֱ�۵���֤������ʾ

����һ����׼�������֤����ܹ����ʺ�ѧϰOAuth2/OIDCЭ��ʵ�ֺ���ҵ����֤ϵͳ������

> ����Իش�����resource/doc/oauth_principle.md�����������

�� Search(pattern: "**/oauth_principle.md")
? ?Found 0 files

�� Bash(find . -name "*oauth*" -type f | grep -i principle)
? ?./flowable-demo/src/main/resources/doc/oauth2_principle.md
./flowable-demo/target/classes/doc/oauth2_principle.md

�� Read(flowable-demo\src\main\resources\doc\oauth2_principle.md)
? ?Read 35 lines (ctrl+o to expand)

�� �ļ�������ʾ�б������⣬�������һ�±����ʽ����������ȷ�ı����ȡ��

�� Bash(file "flowable-demo/src/main/resources/doc/oauth2_principle.md")
? flowable-demo/src/main/resources/doc/oauth2_principle.md: Non-ISO extended-ASCII text, with CRLF line terminato
rs


�� Bash(iconv -f GBK -t UTF-8 "flowable-demo/src/main/resources/doc/oauth2_principle.md" | head -35)
? ?Oauth2�ĺ��Č���

     1.�YԴ�����ߣ�
     �� +25 lines (ctrl+o to expand)

�� �����ҿ��Կ����ļ������ˡ�����һ������OAuth2���Ķ������Ȩ���̵��ĵ���������һЩ���ڻص�URL��ȫ�Ե����ʡ���������
���ĵ�����������⣺

OAuth2ԭ���ĵ��е�������

�ĵ��еĺ�������

�����ĵ�������˼����ؼ����⣬������һ���

1. ϵͳA�Ļص����ȷ���û��Ѿ���Ȩ�ɹ���

��� OAuth2ͨ���ڻص�URL��Я��**��Ȩ��(authorization code)**��ȷ����Ȩ�ɹ���
- ��Ȩ�ɹ���redirect_uri?code=AUTHORIZATION_CODE&state=STATE_VALUE
- ��Ȩʧ�ܣ�redirect_uri?error=access_denied&state=STATE_VALUE

2. �ص�URL�İ�ȫ��֤����

��� OAuth2�ж��ذ�ȫ���Ʒ�ֹ����ص���

a) ����ע��Ļص�URL

- ϵͳBע��ʱ����Ԥ�������Ϸ���redirect_uri
- ϵͳAֻ�����ض�����ע���URL���ܾ�����URL

b) State��������

- �ͻ����������state����
- ��ȨʱЯ����/authorize?...&state=RANDOM_STRING
- �ص�ʱ��֤����鷵�ص�state�Ƿ�ƥ��

c) ��Ȩ����֤

- �ص�URL�е�codeֻ����ʱ��Ȩ��
- ����ͨ����˰�ȫͨ����ȡ������access_token
- ��Ȩ����Ч�ںܶ�(ͨ��10����)��ֻ��ʹ��һ��

3. �ͻ������ʶ��ǰ�û��Ļص���

��� ͨ�����»��ƣ�

a) Session����

// ������Ȩǰ
sessionStorage.setItem('oauth_state', randomState);
// �ص�����
const returnedState = urlParams.get('state');
const originalState = sessionStorage.getItem('oauth_state');
if (returnedState === originalState) {
// ȷ���ǵ�ǰ�û��Ļص�
}

b) State�������û���Ϣ

// ������state�б����û���ʶ
String state = Base64.encode(userId + ":" + timestamp + ":" + random);

4. ��λ��ڻص�URLʵ���û���ص���ҳ��

��� ��׼�������£�

// 1. ���ջص�
const code = urlParams.get('code');
const state = urlParams.get('state');

// 2. ��֤state
if (state !== sessionStorage.getItem('oauth_state')) {
throw new Error('Invalid state');
}

// 3. ��˽���token
const tokenResponse = await fetch('/api/oauth/token', {
method: 'POST',
body: JSON.stringify({ code })
});

// 4. ��ȡ�û���Ϣ
const userInfo = await fetch('/api/user/info', {
headers: { 'Authorization': `Bearer ${accessToken}` }
});

// 5. ��ת���û���ҳ
window.location.href = '/dashboard';

OAuth2��ȫ���ʵ��

1. PKCE (Proof Key for Code Exchange) - ���ڹ����ͻ���
2. Nonce - ��ֹ�طŹ���
3. HTTPSǿ�� - �������䰲ȫ
4. ������Ȩ�� - ����й¶����
5. Refresh Token�ֻ� - ���ڰ�ȫ

�����ĵ���������ⶼ��OAuth2��ȫ��Ƶĺ��Ŀ��ǵ㣬��Щ����ȷ������Ȩ���̵İ�ȫ�Ժ��û���ݵ���ȷʶ��

> ��������»ظ���һ�飬������������ʵʩ���������Ҹ�����ȷ�������˽��������
����oauth2���ĸ�������Դ�����ߣ��ͻ��ˣ���Ȩ����������Դ������
�ĸ���������ʲô������˵�ĸ�������ĸ�����֮������˽��������ǵ��Ⱥ�˳����ʲô��

�� OAuth2��Ȩ�����е��ĸ����Ķ����佻������

�ĸ����Ķ�����

- ��Դ������ (Resource Owner): �û���ӵ���ܱ�����Դ��ʵ��
- �ͻ��� (Client): ϵͳB����Ҫ�����û���Դ�ĵ�����Ӧ��
- ��Ȩ������ (Authorization Server): ϵͳA�������û���֤����Ȩ�ķ�����
- ��Դ������ (Resource Server): �洢�û��ܱ�����Դ�ķ�����

  ---
������Ȩ���̵Ķ��󽻻�

�׶�1: Ԥ��ע��

�ͻ��� �� ��Ȩ������
ϵͳB����Ա��ϵͳAע��Ӧ��:
- �ṩ: Ӧ�����ơ��ص�URL��Ӧ������
- ���: client_id��client_secret

�׶�2: ������Ȩ����

��Դ������ �� �ͻ��� �� ��Ȩ������
1. �û�����ϵͳB�����"ʹ��A�˺ŵ�¼"
2. �ͻ���������Ȩ�����ض����û�:
   GET /authorize?response_type=code
   &client_id=SYSTEM_B_ID
   &redirect_uri=CALLBACK_URL
   &scope=read_profile
   &state=RANDOM_STATE
3. �û���������ض�����Ȩ������

�׶�3: �û���֤����Ȩ

��Դ������ ? ��Ȩ������
1. ��Ȩ��������֤client_id��redirect_uri�ĺϷ���
2. ��Ȩ���������û���ʾ��¼ҳ��
3. �û������˺�������������֤
4. ��Ȩ��������֤�û�ƾ��
5. ��Ȩ��������ʾ��Ȩȷ��ҳ��: "ϵͳB����������ĸ������ϣ��Ƿ�ͬ�⣿"
6. �û����"ͬ����Ȩ"

�׶�4: ������Ȩ��

��Ȩ������ �� �ͻ���
��Ȩ�������ض����û��ؿͻ���:
HTTP/1.1 302 Found
Location: CALLBACK_URL?code=AUTHORIZATION_CODE&state=RANDOM_STATE

�׶�5: ������������

�ͻ��� �� ��Ȩ������
�ͻ��˺�˷���������Ȩ����������:
POST /token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code
&code=AUTHORIZATION_CODE
&redirect_uri=CALLBACK_URL
&client_id=SYSTEM_B_ID
&client_secret=SYSTEM_B_SECRET

��Ȩ������ �� �ͻ���
��Ȩ��������֤�󷵻�:
{
"access_token": "ACCESS_TOKEN",
"token_type": "Bearer",
"expires_in": 3600,
"refresh_token": "REFRESH_TOKEN"
}

�׶�6: �����ܱ�����Դ

�ͻ��� �� ��Դ������
�ͻ���ʹ�÷������������û���Դ:
GET /api/user/profile
Authorization: Bearer ACCESS_TOKEN

��Դ������ �� �ͻ���
��Դ��������֤���ƺ󷵻��û�����:
{
"user_id": "12345",
"name": "����",
"email": "zhangsan@example.com"
}

  ---
�ش����ĵ��еľ�������

1. �ص�URL��ȫ��֤�Ķ��󽻻�

����: ϵͳA�Ļص����ȷ���û��Ѿ���Ȩ�ɹ���

��:
- ��Ȩ������ �ڻص�URL��Я��code������ʾ�ɹ���error������ʾʧ��
- �ͻ��� ͨ�����ص�URL�������ж���Ȩ���

2. ��ֹ����ص��İ�ȫ����

����: ����ص�URL�����Խ���ϵͳB��¼�����ҳ��

��: �����ԣ������ر�����

a) Ԥע����֤

- ��Ȩ������ ֻ����Ԥ��ע���redirect_uri
- �ͻ��� ע��ʱ���������Ϸ��Ļص���ַ

b) State������֤

- �ͻ��� ������Ȩʱ�������state
- �ͻ��� ���ջص�ʱ��֤state�Ƿ�ƥ��

c) ��Ȩ�밲ȫ����

- �ص�URL�е�code ֻ����ʱ��Ȩ�룬���Ƿ�������
- �ͻ��� ����ͨ����˰�ȫͨ������Ȩ����������������access_token

3. �û����ʶ��Ķ��󽻻�

����: ϵͳB���ʶ��ǰ�û��Ļص���

��:
- �ͻ��� ͨ��session��state���������û����
- �ͻ��� ʹ�û�õ�access_token����Դ��������ȡ�û���Ϣ
- �ͻ��� �����û���Ϣչʾ���Ի���ҳ

ʱ��ͼ�ܽ�

��Դ������    �ͻ���        ��Ȩ������      ��Դ������
|          |             |              |
|--����--> |             |              |
|          |--�ض���---> |              |
|<---------|             |              |
|--��¼+��Ȩ-----------> |              |
|<--------�ض���(code)--- |              |
|          |<------------|              |
|          |--����token->|              |
|          |<--token-----|              |
|          |--������Դ-----------------> |
|          |<--�û�����------------------|
|<--��ҳ---|             |              |

ÿ�����趼����ȷ����������Ŀ�����ȷ����������Ȩ���̵İ�ȫ�Ժ��û���ݵ���ȷʶ��

> �׶�2: ������Ȩ����

��Դ������ �� �ͻ��� �� ��Ȩ������
1. �û�����ϵͳB�����"ʹ��A�˺ŵ�¼"
2. �ͻ���������Ȩ�����ض����û�:
   GET /authorize?response_type=code
   &client_id=SYSTEM_B_ID
   &redirect_uri=CALLBACK_URL
   &scope=read_profile
   &state=RANDOM_STATE

������裺
�ͻ���������Ȩ�����ض����û�
��ɶ��˼��
���ض�����Ȩ�����������û���д�˺�������

�� �׶�2��ϸ���ͣ��ͻ���������Ȩ�����ض����û�

"�ض���"�ľ��庬��

�ǵģ������ض�����Ȩ���������û���д�˺����룡

������ϸ����������̣�

����ֽ�

1. �û����"ʹ��A�˺ŵ�¼"

�û���ϵͳB��ҳ���Ͽ�����
[ʹ��΢�ŵ�¼] [ʹ��QQ��¼] [ʹ��GitHub��¼]
�û��������һ����ť

2. �ͻ��ˣ�ϵͳB��������ȨURL

// ϵͳB��ǰ�˻��˴���
function redirectToAuth() {
const authUrl = 'https://auth.systemA.com/authorize?' +
'response_type=code' +
'&client_id=SYSTEM_B_CLIENT_ID' +
'&redirect_uri=https://systemB.com/callback' +
'&scope=read_profile' +
'&state=' + generateRandomState();

      // �ض����û����������Ȩ������
      window.location.href = authUrl;
}

3. ������ض�����Ȩ������

�û��������ַ���ӣ�
https://systemB.com/login

��ת����
https://auth.systemA.com/authorize?response_type=code&client_id=...

4. �û�����Ȩ������ҳ�����

�����û���������ϵͳA�ĵ�¼ҳ�棺
����������������������������������������������������������������������
��        ϵͳA �û���¼            ��
��                                ��
�� �û���: [_______________]        ��
�� ����:   [_______________]        ��
��                                ��
��          [��¼]                 ��
��                                ��
�� ϵͳB����������ĸ�������Ȩ��      ��
��          [ͬ��] [�ܾ�]           ��
����������������������������������������������������������������������

�ؼ�����

�ض����ʵ��

- �ͻ��ˣ�ϵͳB�� �������û���ϵͳA�˺�����
- �ͻ��� ֻ�ǰ��û�"�͵�"��Ȩ������
- �û� ֱ������Ȩ��������ϵͳA�� ����

ΪʲôҪ�ض���

1. ��ȫ��: ϵͳB��Զ���ῴ���û���ϵͳA������
2. ����: �û�����Ϥ��ϵͳA��������ƾ��
3. ����: ϵͳA���Կ�����Ȩ���̺��û�����

ʵ��ʾ���Ա�

��ͳ��¼������ȫ����

�û� �� ϵͳBҳ������A�˺����� �� ϵͳB��֤ ?

OAuth2�ض��򣨰�ȫ����

�û� �� ϵͳB�ض��� �� ϵͳAҳ���������� �� ϵͳA��֤ ?

����ʾ��

�ͻ��������ض���

@Controller
public class LoginController {

      @GetMapping("/login/oauth2")
      public String redirectToAuth() {
          String authUrl = "https://auth.systemA.com/authorize" +
              "?response_type=code" +
              "&client_id=" + clientId +
              "&redirect_uri=" + URLEncoder.encode(redirectUri) +
              "&scope=read_profile" +
              "&state=" + generateState();

          return "redirect:" + authUrl;  // ������ض���
      }
}

�û��������ʵ����ת

1. �û�����: https://systemB.com/login/oauth2
2. ϵͳB����: HTTP 302 �ض�����Ȩ������
3. ������Զ���ת��: https://auth.systemA.com/authorize?...
4. �û�����ϵͳA�ĵ�¼ҳ��

����**"�ͻ���������Ȩ�����ض����û�"**���ǣ�
- ����: ���������Ҫ��������ȨURL
- �ض���: ���û��������ת����Ȩ�������ĵ�¼ҳ��
- Ŀ��: ���û���ϵͳA��������ϵͳB�������˺�����