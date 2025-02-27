<div align="center">

# IntellyCosm Server

</div>

![banner](/docs/images/header.jpg)

### 목차
- [🛠️ IntellyCosm은 이런 기술을 사용했어요](#-intellycosm은-이런-기술을-사용했어요)
    - [For Code](#for-code)
    - [For Infra](#for-infra)
- [🔎 IntellyCosm의 서버 구조](#-intellycosm의-서버-구조)
    - [1️⃣ Service Architecture](#1️⃣-service-architecture)
    - [2️⃣ Infrastructure Architecture](#2️⃣-infrastructure-architecture)

## 🛠️ IntellyCosm은 이런 기술을 사용했어요

### For Code

<img src="https://img.shields.io/badge/Framework-555555?style=for-the-badge">![SpringBoot](https://img.shields.io/badge/springboot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)![spring_data_JPA](https://img.shields.io/badge/spring_data_JPA-%236DB33F?style=for-the-badge&logo=databricks&logoColor=white)![spring_security](https://img.shields.io/badge/spring_security-%236DB33F.svg?style=for-the-badge&logo=springsecurity&logoColor=white)

<img src="https://img.shields.io/badge/build-555555?style=for-the-badge">![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)

<img src="https://img.shields.io/badge/Test-555555?style=for-the-badge">![junit5](https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)![mockito](https://img.shields.io/badge/mockito-DA383E?style=for-the-badge&logo=mockito&logoColor=white)

<img src="https://img.shields.io/badge/Database-555555?style=for-the-badge">![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)

### For Infra

<img src="https://img.shields.io/badge/Computing-555555?style=for-the-badge">![Amazon Ec2](https://img.shields.io/badge/amazon_ec2-FF9900.svg?style=for-the-badge&logo=amazonec2&logoColor=white)

<img src="https://img.shields.io/badge/CI/CD-555555?style=for-the-badge">![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)

<img src="https://img.shields.io/badge/Data_Storage-555555?style=for-the-badge">![Amazon S3](https://img.shields.io/badge/AWS_S3-569A31.svg?style=for-the-badge&logo=amazons3&logoColor=white)![Amazon RDS](https://img.shields.io/badge/amazon_RDS-527FFF.svg?style=for-the-badge&logo=amazonrds&logoColor=white)![Amazon ElastiCache](https://img.shields.io/badge/amazon_elasticache-C925D1.svg?style=for-the-badge&logo=amazonelasticache&logoColor=white)

<img src="https://img.shields.io/badge/Networking-555555?style=for-the-badge">![Route 53](https://img.shields.io/badge/amazon_rount_53-8C4FFF.svg?style=for-the-badge&logo=amazonroute53&logoColor=white)![Amazon ALB](https://img.shields.io/badge/amazon_alb-8C4FFF.svg?style=for-the-badge&logo=awselasticloadbalancing&logoColor=white)

<img src="https://img.shields.io/badge/Security-555555?style=for-the-badge">![AWS IAM](https://img.shields.io/badge/aws_iam-FF9900.svg?style=for-the-badge&logo=amazoniam&logoColor=white)![AWS Secrets Manager](https://img.shields.io/badge/aws_secrets_manager-DD344C?style=for-the-badge&logo=awssecretsmanager&logoColor=white)

## 🔎 IntellyCosm의 서버 구조

### 1️⃣ Service Architecture

![Service Architecture](/docs/images/service-architecture.png)

IntellyCosm은 Clean Architecture를 기반으로 설계되었습니다:

- **Controller Layer**: 외부 요청을 처리하고 응답을 반환합니다.
- **Service Layer**: 비즈니스 로직을 처리하고 도메인 객체를 관리합니다.
- **Repository Layer**: 데이터베이스와의 상호작용을 담당합니다.
- **Domain Layer**: 핵심 비즈니스 로직과 규칙을 포함합니다.

주요 특징:
- 계층 간 명확한 경계와 책임 분리
- 도메인 중심 설계로 비즈니스 로직 응집도 향상
- 의존성 주입을 통한 결합도 감소

### 2️⃣ Infrastructure Architecture

![Infrastructure Architecture](/docs/images/infra-architecture.png)

- **Load Balancing**: AWS ALB를 통한 트래픽 분산
- **Auto Scaling**: EC2 Auto Scaling으로 탄력적인 서버 운영
- **Cache Layer**: Redis를 활용한 성능 최적화
- **Storage**: S3를 통한 이미지 저장 및 관리
- **Database**: RDS(MySQL)를 통한 안정적인 데이터 관리
