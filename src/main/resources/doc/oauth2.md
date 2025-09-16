�� ? OAuth2�˜ʶ��c��Ƚ���

? 1. �Q����������ʲ�N��Ҫ�@Щ���c��

�vʷ����

OAuth2��2012����ʽ�l�ѵ��ڙ��˜ʅf�h(RFC 6749)����춽�Q���������ð�ȫ�L���Ñ��YԴ�Ć��}��

���}�����e����
�Ñ���׌"���D����"�L������"΢��"�e����Ƭ
? ���y��ʽ���Ñ���΢���ܴa���V���D���� (�O����ȫ!)
? OAuth2��ʽ��΢���ڙ����D�����L����Ƭ������¶�ܴa

����������

1. ��ȫ�ԣ����������ò��ܽ��|�Ñ��ܴa
2. �ڙྫ�_�ԣ��Ñ����Ծ��_�����ڙ๠��
3. �ɳ��N�ԣ��Ñ��S�r���Գ��N�ڙ�
4. �˜ʻ�������OAuth2�����̶���ѭ��ͬ�f�h

�� ? 2. �����c��OAuth2�����еĽ�ɫ������

����OAuth2�ڙ�a���̈D��

sequenceDiagram
participant User as ?�Ñ�
participant Client as ?����������
participant AuthServer as ??�ڙ������
participant Resource as ??�YԴ������

      Note over User,Resource: 1. ���հl�F�A��
      Client->>AuthServer: GET /.well-known/oauth-authorization-server
      AuthServer-->>Client: �������ж��c������Ϣ   

      Note over User,Resource: 2. �ڙ��A��
      User->>Client: �c��"΢�ŵ��"
      Client->>AuthServer: GET /oauth2/authorize?response_type=code&client_id=xxx
      AuthServer->>User: �@ʾ�ڙ�_�J���
      User->>AuthServer: �_�J�ڙ�
      AuthServer-->>Client: 302�ض���: callback?code=xxx

      Note over User,Resource: 3. ���ƽ��Q�A��
      Client->>AuthServer: POST /oauth2/token (�ڙ�a+�͑��ˑ{��)
      AuthServer->>AuthServer: ��C�ڙ�a�Ϳ͑���
      AuthServer-->>Client: ����access_token+refresh_token

      Note over User,Resource: 4. �YԴ�L���A��
      Client->>Resource: GET /api/photos (Bearer access_token)
      Resource->>AuthServer: POST /oauth2/introspect (��C����)
      AuthServer-->>Resource: ������Ч+���޹���
      Resource-->>Client: ������Ƭ����

      Note over User,Resource: 5. �Ñ���Ϣ�@ȡ(OIDC)
      Client->>AuthServer: GET /oauth2/userinfo (Bearer access_token)
      AuthServer-->>Client: �����Ñ�������Ϣ

�����cԔ����ɫ����

| ���c                                      | ��ɫ���       | ���Õr�C   | ���Ĺ���                 |
  |-----------------------------------------|------------|--------|----------------------|
| /.well-known/oauth-authorization-server | ?���հl�F��    | �����_ʼǰ  | ���V�͑���"��֧����Щ���ܡ����c�����e" |
| /oauth2/jwks                            | ?��耹�����    | ������C�r  | �ṩJWT������C����Ĺ��       |
| /oauth2/authorize                       | ??��?�ڙ����T�T | �Ñ��ڙ��r  | ̎���Ñ��ڙ�_�J�������ڙ�a       |
| /oauth2/token                           | ?���ƃ��Q��    | ���ƫ@ȡ�r  | ���ڙ�a���Q�L������           |
| /oauth2/introspect                      | ???��?���Ƃ�̽ | �YԴ�L���r  | ��C�����Ƿ���Ч�����޹���        |
| /oauth2/userinfo                        | ?��Ϣ�ṩ��    | OIDC���� | �ṩ�Ñ�������Ϣ���������]��ȣ�     |

�� ?? 3. ��ʲ�NSpring SecurityҪ���b�@Щ���c��

A. �˜ʅf�h���}�s��

OAuth2 RFC 6749�˜ʷǳ��}�s��
- 4�N��ͬ���ڙ�����(grant types)
- ����İ�ȫҪ��(PKCE, state������)
- �}�s���e�`̎��C��
- JWT���Ƶ����ɡ���������C
- �͑����J�C�Ķ�N��ʽ
- OIDC�f�h�ĔUչ֧��

���׌�_�l���Լ����F����Ҫ̎��
- ? ����퓵�RFC�ęn���
- ? �}�s�ļ����㷨���F
- ?? ���N��ȫ©�����o
- ? ��N���̵Ġ�B����
- ?? Ԕ�����e�`�a���e�`̎��

B. ��ȫ�Կ���

OAuth2�漰�İ�ȫ���}��
// �@Щ��ȫ��������_�l���Լ����F�����׳��e��

1. �ڙ�a�طŹ������o
2. CSRF�������o(state����)
3. PKCE�������o
4. �͑����J�C��ʽ�x��
5. JWT���ƺ����㷨��ȫ��
6. �����^�ڕr�g����
7. ���޹�����C
8. ����Ո��̎��

Spring Security�Č��I���F��
- ? ���^������ȫ���Ҍ���
- ? ���m�İ�ȫ©���ޏ�
- ? �������°�ȫ��ь��`
- ? �Ԅ�̎����N�������o

C. �������Ա��C

�˜ʻ�����Ҫ�ԣ�
Google OAuth2    ����  Spring Security OAuth2
΢�� OAuth2      ����  Spring Security OAuth2
GitHub OAuth2    ����  Spring Security OAuth2
Facebook OAuth2  ����  Spring Security OAuth2

���ÿ���_�l���Լ����F��
- ? ���ܲ����Ϙ˜ʣ����»������Ԇ��}
- ? �͑���SDK�o��ͨ��
- ? ���������ý������y
- ? �f�h�������ƫ��

D. �_�lЧ������

Spring Security���b����ĺ�̎��

// ? �Լ����F��Ҫ���Ĵ��a����
@RestController
public class OAuth2Controller {

      // ��Ҫ���F��ǧ�д��a��
      @GetMapping("/oauth2/authorize")
      public ResponseEntity<?> authorize(
          @RequestParam String response_type,
          @RequestParam String client_id,
          @RequestParam String redirect_uri,
          @RequestParam String scope,
          @RequestParam String state,
          @RequestParam(required=false) String code_challenge,
          @RequestParam(required=false) String code_challenge_method,
          HttpServletRequest request) {

          // 1. ��C���Ѕ����ĺϷ���
          // 2. ��C�͑����Ƿ���ںͻ��S
          // 3. ��C�ض���URI�Ƿ�ƥ��
          // 4. ̎��PKCE challenge
          // 5. �z���Ñ��Ƿ��ѵ��
          // 6. �����ڙ�����ֱ���ڙ�
          // 7. �����ڙ�a�K�惦
          // 8. ̎����N�e�`��r
          // 9. �ض��򵽿͑���...
          // (߀�и����}�s߉݋)
      }

      @PostMapping("/oauth2/token")
      public ResponseEntity<?> token(/*�����}�s���F*/) { }

      @PostMapping("/oauth2/introspect")
      public ResponseEntity<?> introspect(/*�����}�s���F*/) { }

      // ... �������c
}

// ? ʹ��Spring Securityֻ��Ҫ��
@Configuration
public class OAuth2Config {

      @Bean
      public RegisteredClientRepository clientRepository() {
          // �������ÿ͑�����Ϣ
      }

      @Bean
      public JWKSource<SecurityContext> jwkSource() {
          // ��������JWT�������
      }
}

// ���ж��c�Ԅ����ɣ���ǧ�д��a׃�Ɏ�ʮ�����ã�

E. ��I������֧��

Spring Security߀�ṩ����I�����ԣ�
- ? ��Ⱥ֧�� - �ց�ʽsession����
- ? �O��ָ�� - �cActuator����
- ? ��N�J�C��ʽ - LDAP�������졢JWT��
- ? ���H��֧�� - ���Z���e�`��Ϣ
- ? �ɔUչ�ܘ� - ���S�Զ��x���N�M��
- ? ��Ӌ���I - Ԕ���İ�ȫ�¼�ӛ�

�� ? ���Y��OAuth2���c�ı�Ҫ�Ժͷ��bԭ��

? ���Ļش����Ć��}��

1. �Q����������Q��ȫ�ڙ���ИIʹ�c

- ���}��������������Ҫ��ȫ�L���Ñ��YԴ�������ܫ@ȡ�Ñ��ܴa
- ��Q������OAuth2�˜ʅf�h��ͨ�^���ƙC�ƌ��F��ȫ�ڙ�
- ��Ҫ�ԣ��F�����W���õĻ��A��ȫ����

2. ��ɫ���ã�OAuth2�����е��P�I�h��

ÿ�����c����OAuth2�����в���ȱ�ٵ�һ�h��
- �l�F���� �� �@ȡ�ڙ� �� ���Q���� �� ��C�L�� �� �@ȡ��Ϣ
- ȱ���κ�һ���h��������OAuth2���̾͟o����������

3. Spring Security���b��ԭ��

? ��ȫ�� - ��ֹ�_�l�ߌ��F��ȫ©��
? �˜ʻ� - �_���f�h���F�����_�Ժͻ�������? Ч�� - ��ǧ���}�s���a׃�Ɏ�ʮ�к�������
? ��I�� - �ṩ���a�h����Ҫ�ĸ߼�����

? ���⣺

�����w���ӣ�
- ? �Լ����F = �Լ����u�����ࡢ����Ȳ����I�����׳����}
- ? Spring Security = ʹ�Ø˜ʻ����A�Ƙ�������ȫ�ɿ��Ҹ�Ч

OAuth2���c�����ӵĘ˜ʘ�����
- ? �T��authorize���c��- �����L�����
- ? 耳׹���ϵ�y��token���c��- �ְl�L���{�C
- ??��? ������introspect���c��- ��C�L������
- ? ��Ϣ̨��userinfo���c��- �ṩ��Ҫ��Ϣ