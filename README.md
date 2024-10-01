# Good Bite <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/good-bite-logo-simplify-removebg.png" width="25"/> Back-end

<div align="center">
    <a href="https://goodbite.site">
        <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/good-bite-logo-removebg-preview.png" alt="logo" width="500"/>
    </a>
</div>

> **🔗홈페이지 링크: https://goodbite.site**
> </br>
> **🔗프론트엔드 깃허브 페이지: https://github.com/nbc-team-gumi/goodbite-react**

## <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/good-bite-logo-simplify-removebg.png" width="25"/> 프로젝트 소개

가게에서 손님의 대기 순번을 관리하고, 차례가 되었을 때 그 손님들에게 알려줄 수 있는 시스템을 개발합니다.

이 시스템은 손님들이 대기 시간을 효율적으로 관리할 수 있도록 도와주며, 가게 운영자와 관리자에게도 편리한 도구를 제공합니다.
손님은 대기 순번을 실시간으로 확인할 수 있으며, 자신의 차례가 다가오면 알림을 받게 됩니다.
가게 측에서는 손님의 대기 순번을 체계적으로 관리하고, 손님들에게 원활한 서비스를 제공할 수 있습니다.

## ⏱️ 개발 기간

> **내일배움캠프 Java 5기 팀프로젝트**
>
> **2024.07.17 ~ 2024.08.20 (5주)**
>   - 2024.08.05: 중간 발표회
>   - 2024.08.21: 최종 발표회

# <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/My-Gummy-logo-simplify-removebg.png" width=35/>팀 마이구미 [<img src="https://img.icons8.com/?size=100&id=CexFs1lac6J7&format=png&color=000000" width="30">](https://github.com/nbc-team-gumi)

|                                        강현지                                        |                                        김정민                                        |                                       김현성                                       |                                       양소영                                       |                                       이하은                                        |                                                                                                              
|:---------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------:|
| <img width="160px" src="https://avatars.githubusercontent.com/u/102335813?v=4" /> | <img width="160px" src="https://avatars.githubusercontent.com/u/112466204?v=4" /> | <img width="160px" src="https://avatars.githubusercontent.com/u/66352581?v=4"/> | <img width="160px" src="https://avatars.githubusercontent.com/u/72538861?v=4"/> | <img width="160px" src="https://avatars.githubusercontent.com/u/166499347?v=4"/> |
|                      [@onda0-0](https://github.com/onda0-0)                       |                [@sillysillyman](https://github.com/sillysillyman)                 |                [@kim201621123](https://github.com/kim201621123)                 |                 [@a-white-bit](https://github.com/a-white-bit)                  |                     [@haeuni00](https://github.com/haeuni00)                     |
|                                사용자 도메인, 성능 최적화, 캐싱                                |                              리뷰 기능, 가게 메뉴, 가게 예약 구현                               |                             가게 웨이팅 기능, 알림 기능 (SSE)                              |                              사용자 인증/인가, 배포 아키텍처 관리                              |                       가게 도메인, 영업시간 기능 구현, 이미지 클라우드(S3) 관리                        |

# <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/good-bite-logo-simplify-removebg.png" width="25"/> 가이드

## Requirements

For building and running the application you need:

- JDK 21: Amazon Corretto 21.0.4 recommended
- Gradle 8.8 recommended

## Environment arguments

```
// mysql database
DB_HOST={host_url}
DB_NAME={db_name}
DB_PASSWORD={db_password}
DB_PORT={db_port}
DB_USERNAME={username}

// cors allowed origin url
DOMAIN_URL={domain_url}
SUBDOMAIN_URL={subdomain_url}
ELB_DNS_FRONT={dns_url}

// cloud hosting
EC2_HOST={host_url}

// key
JWT_SECRET_KEY={secret_key}
KAKAO_API_KEY={secret_key}
PUBLIC_DATA_KEY={secret_key}
SSL_KEY={ssl_password}

// redis
REDIS_PASSWORD={redis_password}
REDIS_PORT={redis_port}
REDIS_SERVER={host_url}

// s3
S3_ACCESS_KEY={access_key}
S3_BUCKET_NAME={s3_name}
S3_REGION={s3_region}
S3_SECRET_KEY={secret_key}
```

# <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/good-bite-logo-simplify-removebg.png" width="25"/> 기술 스택

### 개발 환경

| Type          | Tech                                                                                                                                                                                                                                                                                                                                                                                                                                   | 
|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| IDE           | ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)                                                                                                                                                                                                                                                                                                          |
| Framework     | ![Spring](https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![Spring](https://img.shields.io/badge/SpringBoot_3.3.2-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)                                                                                                                                                                                                       |
| Language      | ![Java](https://img.shields.io/badge/java_JDK21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)                                                                                                                                                                                                                                                                                                                        |
| Security      | ![security](https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white) ![JWT](https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white) ![SSL](https://img.shields.io/badge/openssl-721412?style=for-the-badge&logo=openssl&logoColor=white) ![OAuth](https://img.shields.io/badge/OAuth2.0-721412?style=for-the-badge&logoColor=white)         |
| Database      | ![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white) ![RDS](https://img.shields.io/badge/rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white)                                                                                                                                                                                                                              |
| Build         | ![Gradle](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)                                                                                                                                                                                                                                                                                                                                  |
| Cashing       | ![Redis](https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white)                                                                                                                                                                                                                                                                                                                                     |
| Messaging     | ![SSE](https://img.shields.io/badge/sse-000000?style=for-the-badge&logo=server&logoColor=white) ![WebSocket](https://img.shields.io/badge/websocket-000000?style=for-the-badge&logo=websocket&logoColor=white)                                                                                                                                                                                                                         |
| DevOps        | ![AWS](https://img.shields.io/badge/aws-FF9900?style=for-the-badge&logo=amazonwebservices&logoColor=white) ![githubactions](https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)                                                                                     |
| Cloud Infra   | ![EC2](https://img.shields.io/badge/EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white) ![ELB](https://img.shields.io/badge/ELB-8C4FFF?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white) ![Route53](https://img.shields.io/badge/Route53-8C4FFF?style=for-the-badge&logo=amazonroute53&logoColor=white) ![S3](https://img.shields.io/badge/s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)     |
| Collaboration | ![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white) ![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white) ![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white) ![Slack](https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white) |

# <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/good-bite-logo-simplify-removebg.png" width="25"/> 주요 기능

> ### 계정
> - 같은 계정으로 총 두 종류의 계정 가입이 가능합니다.
> - 예시) my_email@email.com 손님 계정과, my_email@email.com 가게 계정은 별개
> - 가게 관리자 계정은 **사업자 번호가 필요**합니다.
>
> ### 손님 (일반 사용자) 기능:
> - 웨이팅 등록 **웨이팅: 가게에 즉시 대기 순번을 부여받고 차례 시에 입장가능*
> - 예약 등록 **예약: 영업시간 중 원하는 시간대에 예약하고 입장 확정*
> - 리뷰 등록
> - 웨이팅, 예약 취소
> - 알림: 입장, 취소 알림
>
> ### 가게 관리자 기능:
> - 가게 등록 (최대 1개)
> - 가게 정보 수정 **사업자 번호 수정 불가*
> - 메뉴 등록
> - 웨이팅 관리: 수락, 취소
> - 예약 관리: 수락, 취소
> - 알림: 신규 웨이팅, 예약, 취소 알림

# <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/good-bite-logo-simplify-removebg.png" width="25"/> API 명세서

🔗 API 명세서 노션 페이지 링크

<a href="https://teamsparta.notion.site/3eb86cddac8f4123bf9a677f34e30671?v=08d7113eab5b4f0488b30fd0e76f5f2c">
<img src="https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white" width="100"/>
</a>

# <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/good-bite-logo-simplify-removebg.png" width="25"/> 서비스 아키텍처

<div align="center">
        <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/goodbite_infra.png" alt="architecture"/>
</div>

# <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/good-bite-logo-simplify-removebg.png" width="25"/> ERD

<div align="center">
    <img src="https://goodbite-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B4%80%EB%A6%AC%EC%9E%90+%EC%9D%B4%EB%AF%B8%EC%A7%80/erd.png" alt="erd"/>
</div>