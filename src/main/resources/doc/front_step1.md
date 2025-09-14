
�� ?? ����ǰ�˴���ṹ��ϸ����

? ��ĿĿ¼�ṹ����

flowable-demo-ui/
������ .git/                    # Git�汾����Ŀ¼
������ .vite/                   # Vite����Ŀ¼����ʷ������
������ .gitignore              # Git�����ļ�����
������ package.json            # Node.js��Ŀ�����ļ�
������ package-lock.json       # ���������ļ�
������ server.js              # ? Node.js��̬�����������ģ�
������ index.html             # ? ��ҳ���ļ�
������ style.css              # ? ��ʽ�ļ�
������ script.js              # ? JavaScript�߼��ļ�

? ���ļ����ܺ��������

1. ? package.json - ��Ŀ��������

{
"name": "flowable-demo-ui",           // ��Ŀ����
"version": "1.0.0",                   // �汾��
"description": "Flowable Demo Frontend", // ��Ŀ����
"main": "server.js",                  // ? ����ļ�ָ��server.js
"scripts": {
"start": "node server.js",          // ? ������������
"dev": "node server.js"            // ? ������������
},
"keywords": ["flowable", "workflow", "demo", "frontend"],
"license": "MIT"
}
����: ������Ŀ������Ϣ�������ű���û���κ��ⲿ����

2. ?? server.js - Node.js��̬�ļ����������ܹ����ģ�

const server = http.createServer((req, res) => {
const urlPath = req.url.split('?')[0];        // ����URL·��

      // ? �ؼ��߼���SPA·��֧��
      if (path.extname(urlPath)) {
          var filePath = path.join(__dirname, urlPath); // ��̬�ļ�ֱ�ӷ���
      } else {
          var filePath = path.join(__dirname, 'index.html'); // SPA·�ɻ���
      }

      // MIME�������ú��ļ�����...
});

���Ĺ���:
- ��̬�ļ�����: �ṩHTML��CSS��JS��ͼƬ���ļ�
- SPA·��֧��: /callback������չ��·��������index.html
- MIME���ʹ���: ��ȷ���ø����ļ���Content-Type
- ������: 404��500������Ѻ���ʾ
- ���ж˿�: 3000������8080���룩

Ϊʲô�������?
- ? ������: ����ҪExpress�ȿ�ܣ���Node.js
- ? SPA�Ѻ�: ֧��ǰ��·�ɣ�OAuth2�ص�URL������ȷ����
- ? ������: һ���ļ�������з�������

3. ? index.html - ��ҳ��Ǽ�

  <!DOCTYPE html>
  <html>
  <head>
      <meta charset="UTF-8">
      <title>Flowable Demo</title>
      <link rel="stylesheet" href="style.css">    <!-- ��ʽ���� -->
  </head>
  <body>
      <div class="container">
          <h1>Flowable Demo</h1>
          <p>������������ʾ�Ŀ</p>                    <!-- ��ǰ���ݼ��� -->
      </div>
      <script src="script.js"></script>           <!-- JS�߼����� -->
  </body>
  </html>

��ǰ״̬: ���򻯵�չʾҳ��
δ������: OAuth2�ͻ��˵�����������

4. ? style.css - ������ʽϵͳ

body {
font-family: Arial, sans-serif;  // ��������
margin: 0;
padding: 20px;
background: #f5f5f5;            // ǳ�ұ���
}

.container {
max-width: 800px;               // ��Ӧʽ����
margin: 0 auto;                 // ���ж���
background: white;              // ��ɫ������
padding: 20px;
border-radius: 5px;            // Բ�����
}

����ص�:
- ��С����ʽ: ֻ�����������ֺ���ɫ
- ��Ӧʽ���: 800px����ȣ����䲻ͬ��Ļ
- �ִ������: ��ɫ��Ƭʽ���
- ����չ��: Ϊ����OAuth2����Ԥ����ʽ����

5. ? script.js - JavaScript�߼��ļ�

// ��С���_��
console.log('Flowable Demo ���d��');

��ǰ״̬: �����հף����е������
δ������: OAuth2�ͻ��˵ĺ����߼�ʵ�ֵ�

?? �����ܹ�����

�ܹ��ص�

? �ܹ�����: ����̬ + Node.js������
? ����ջ: HTML + CSS + ԭ��JavaScript + Node.js
? ��������: ���ⲿ����
? ����ģʽ: ��̬�ļ����� + SPA·��֧��
? ����ģʽ: �޸ļ���Ч���޹�������

���Ʒ���

- ? ����ܹ�: û�и��ӵĹ������ߺͿ������
- ? ��������: �����Ӽ�����������
- ? �������: ����ṹ�������ʺ�ѧϰOAuth2
- ? ά����: ����Ҫ�����ܰ汾��������

��ǰ����

- ? ���ܼ���: Ŀǰֻ�Ǿ�̬չʾ��û��ʵ��ҵ���߼�
- ? �������: û���ִ�ǰ�˿�ܵ������֧��
- ? ��״̬����: û�и���Ӧ����Ҫ��״̬�������

? ΪOAuth2������������

���д����OAuth2�Ѻ�����

1. SPA·��֧�� - server.js:13-15 �Ѿ�֧��/callback·��
2. �����˿� - 3000�˿�����8080���룬����OAuth2���ʵ��
3. ��̬���� - ���԰�ȫ�ش���OAuth2�ض��������
4. ����ṹ - �������OAuth2��ص�JavaScript�߼�

��Ҫ��ӵ�OAuth2����

1. OAuth2�ͻ��˿� - ������Ȩ������
2. ���ƹ��� - �洢��ʹ��access_token
3. API���� - ʹ�����Ʒ��ʺ����Դ
4. �û����� - ��¼��ť���û���Ϣ��ʾ��

����򵥶������ļܹ�Ϊ����ʵ��OAuth2�ͻ����ṩ������Ļ����� ?

> ��o������һ�®�ǰ�Ŀ��ȱ����ɶ��
ǰ���ĿĿǰȱʲ�N�� Ҫ��Ό��F��
���Ƚo����һ���ķ����Ҍ������ٛQ��Ҫ��Ҫ����

�� ? ��ǰ�Ŀȱ�ݷ���

�� ? ��ǰ�Ŀ����Ҫȱ��

1. ������ȱ�ݣ�����ȱ�ݣ�

? �oOAuth2�͑��˹��� - �Ŀ����Ŀ��δ���F
? �o�Bչʾ��� - �]���κ��Ñ�����
? �oAPI�{������ - �o���c���OAuth2������ͨ��
? �o���ƹ��� - �o���惦��ʹ��access_token
? �o·��̎�� - �o��̎��OAuth2���{����

2. �ܘ���ȱ��

?? �o��B���� - �o��׷���Ñ���䛠�B
?? �o�e�`̎�� - �oOAuth2���̮���̎��C��
?? ��ȫ�Բ��� - �oPKCE��state�����Ȱ�ȫ��ʩ
?? �o���ش惦 - �o���־û��Ñ�����

�� ? ǰ���Ŀȱʧ��OAuth2�͑��˹���

����ȱʧ������Σ�

| ȱʧ����       | ��Ҫ��   | ��ǰ��B | Ӱ�               |
  |------------|-------|------|------------------|
| OAuth2�ڙ�l�� | ? ���� | ? �o  | �o���_ʼOAuth2����     |
| �ڙ���{̎��     | ? ���� | ? �o  | �o�������ڙ�a          |
| ���ƽ��Q       | ? ���� | ? �o  | �o���@ȡaccess_token |
| �Ñ���B����     | ? ��Ҫ | ? �o  | �o��֪���Ñ���䛠�B       |
| API�{��      | ? ��Ҫ | ? �o  | �o���L���ܱ��o�YԴ        |
| ��ȫ��ʩ       | ? ��Ҫ | ? �o  | ���ڰ�ȫ�L�U           |

? ��һ�����F����

�� ���h���F���������A�Σ�

? �A��һ�����AOAuth2�͑��ˌ��F ?

1.1 ����OAuth2�͑����

// oauth2-client.js - �½��ļ�
class OAuth2Client {
constructor() {
this.clientId = 'test_client_001';  // ʹ�ìF�еĜyԇ�͑���
this.redirectUri = 'http://localhost:3000/callback';
this.authServerUrl = 'http://localhost:8080';
this.scopes = ['read', 'write', 'openid', 'profile'];
}

      // �l��OAuth2�ڙ�
      authorize() { /* ���F�ڙ����D߉݋ */ }

      // ̎���ڙ���{
      handleCallback() { /* ̎���ڙ�a */ }

      // ���Q����
      exchangeToken(code) { /* �ڙ�a�Qȡ���� */ }
}

1.2 ����������

  <!-- index.html ���� -->
  <div class="container">
      <h1>OAuth2 Demo</h1>

      <!-- δ��䛠�B -->
      <div id="loginSection">
          <button id="loginBtn">OAuth2 ���</button>
      </div>

      <!-- �ѵ�䛠�B -->
      <div id="userSection" style="display: none;">
          <h3>�Ñ���Ϣ</h3>
          <div id="userInfo"></div>
          <button id="logoutBtn">�ǳ�</button>
      </div>
  </div>

1.3 ���F��������߉݋

// script.js ����
const oauth2Client = new OAuth2Client();

// �����d�r�z��URL�Ƿ�����ڙ���{
window.addEventListener('load', () => {
const urlParams = new URLSearchParams(window.location.search);
if (urlParams.has('code')) {
oauth2Client.handleCallback();  // ̎��OAuth2���{
} else {
checkLoginStatus();  // �z���䛠�B
}
});

// ��䛰��o�¼�
document.getElementById('loginBtn')?.addEventListener('click', () => {
oauth2Client.authorize();  // �l��OAuth2�ڙ�
});

? �A�ζ����������܌��F ?

2.1 ���ƹ���

- localStorage�惦access_token��refresh_token
- �����^���Ԅәz�y��ˢ��
- ��ȫ����������C��

2.2 API�{�ù���

- ʹ��access_token�{������ܱ��o�YԴ
- �yһ��APIՈ����b
- �e�`̎�����ԇ�C��

2.3 �Ñ���Ϣչʾ

- �{��/oauth2/userinfo���c�@ȡ�Ñ���Ϣ
- չʾ�Ñ������Y��
- ���޹����@ʾ

? �A��������ȫ�̓��� ??

3.1 ��ȫ����

- PKCE (Proof Key for Code Exchange) ���F
- state������CSRF����
- ��ȫ�����ƴ惦����

3.2 �Ñ��w򞃞��

- ���d��Bָʾ
- �e�`��Ϣ�Ѻ�չʾ
- 푑�ʽ�OӋ����

? ���]���Ѓ��ȼ�

? ��һ���ȼ����������F��

1. ����OAuth2Client� - �����ڙ�߉݋
2. ����������HTML - ��䛰��o�͠�B�@ʾ
3. ���F���A�ڙ����� - ���D���ڙ������

�A���r�g: 2-3С�r
�L�U: �ͣ���춬F�����OAuth2������

? �ڶ����ȼ������m���F��

4. ���ƽ��Q���� - �ڙ�a�Qȡaccess_token
5. �Ñ���B���� - localStorage + ��B�ГQ
6. ���AAPI�{�� - �yԇ�ܱ��o�YԴ�L��

�A���r�g: 3-4С�r
�L�U: �еȣ���Ҫ̎���������ƹ���

? �������ȼ��������A�Σ�

7. ��ȫ��ʩ - PKCE + state����
8. �e�`̎�� - �Ѻõ��e�`��ʾ
9. UI/UX���� - ����������Ñ��w�

�A���r�g: 2-3С�r
�L�U: �ͣ���Ҫ�ǹ�������

?? ��ʩǰ�Ĝʂ乤��

1. �_�����OAuth2�����������\�� (8080�˿�)
2. �_�J�yԇ�͑��˔��� (test_client_001)
3. �yԇ�����O�� (ǰ��3000 -> ���8080)

��ϣ�����_ʼ��ʩ�Ă��A�Σ����h���A��һ�Ļ��AOAuth2�͑��ˌ��F�_ʼ�� ?