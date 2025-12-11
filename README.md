# 🏠⚡ Home Energy Management System

Sistema de gerenciamento de energia residencial baseado em microsserviços, desenvolvido com Spring Boot, Kafka e MySQL. O sistema permite monitoramento em tempo real do consumo energético de dispositivos IoT domésticos.

---

## 📋 Sumário

- [Visão Geral](#-visão-geral)
- [Arquitetura](#-arquitetura)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação e Configuração](#-instalação-e-configuração)
- [Executando o Projeto](#-executando-o-projeto)
- [API Endpoints](#-api-endpoints)
- [Simulação de Dados](#-simulação-de-dados)
- [Estrutura do Banco de Dados](#-estrutura-do-banco-de-dados)
- [Monitoramento](#-monitoramento)
- [Troubleshooting](#-troubleshooting)

---

## 🎯 Visão Geral

O **Home Energy Management System** é uma solução completa para monitoramento e análise de consumo energético em ambientes residenciais. O sistema é composto por três microsserviços principais:

### Microsserviços

1. **User Service** (Porta 8080)
   - Gerenciamento de usuários
   - Configuração de alertas de consumo
   - Definição de thresholds energéticos

2. **Device Service** (Porta 8081)
   - CRUD de dispositivos IoT
   - Suporte para múltiplos tipos: SPEAKER, CAMERA, THERMOSTAT, LOCK, LIGHT, DOORBELL
   - Associação de dispositivos com usuários

3. **Ingestion Service** (Porta 8082)
   - Ingestão de dados de consumo energético
   - Publicação de eventos no Kafka
   - Simuladores de carga (sequencial e paralelo)

---

## 🏗️ Arquitetura

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│  User Service   │      │ Device Service  │      │Ingestion Service│
│   (Port 8080)   │      │   (Port 8081)   │      │   (Port 8082)   │
└────────┬────────┘      └────────┬────────┘      └────────┬────────┘
         │                        │                         │
         └────────────────────────┼─────────────────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │      MySQL Database       │
                    │       (Port 3307)         │
                    └───────────────────────────┘
                                  
         ┌────────────────────────┴─────────────────────────┐
         │                                                   │
    ┌────▼─────┐                                      ┌─────▼─────┐
    │  Kafka   │◄─────────────────────────────────────┤ Zookeeper │
    │(Port 9092)│                                      │(Port 2181)│
    └──────────┘                                       └───────────┘
         │
    ┌────▼─────┐
    │ Kafka UI │
    │(Port 8070)│
    └──────────┘
```

### Fluxo de Dados

1. Dispositivos enviam dados de consumo → **Ingestion Service**
2. Ingestion Service publica eventos → **Kafka Topic** (`energy-usage`)
3. Eventos são consumidos por processadores downstream (futuro)
4. Dados persistidos no **MySQL** via Flyway migrations

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21** (Eclipse Temurin)
- **Spring Boot 3.x**
  - Spring Web
  - Spring Data JPA
  - Spring Kafka
  - Spring AOP (Logging e Performance)
- **Lombok** (Redução de boilerplate)

### Infraestrutura
- **MySQL 8.0.44** (Banco de dados relacional)
- **Apache Kafka 7.6.0** (Message broker)
- **Zookeeper 3.8** (Coordenação do Kafka)
- **Kafka UI** (Interface de gerenciamento)
- **Docker & Docker Compose** (Containerização)

### Ferramentas
- **Flyway** (Migrações de banco de dados)
- **HikariCP** (Connection pooling)
- **AspectJ** (AOP para logging)

---

## ✅ Pré-requisitos

- **Docker** 20.10+ e **Docker Compose** 1.29+
- **Java 21** (para desenvolvimento local)
- **Maven 3.8+** (para build)
- **Git**
- **4GB RAM** disponível (mínimo recomendado)

### Verificação de Versões

```bash
docker --version
docker-compose --version
java -version
mvn -version
```

---

## 📦 Instalação e Configuração

### 1. Clone o Repositório

```bash
git clone <repository-url>
cd home-energy-management
```

### 2. Estrutura do Projeto

```
home-energy-management/
├── user-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── device-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── ingestion-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
├── .env
└── init.sql
```

### 3. Configuração de Variáveis de Ambiente

Crie/edite o arquivo `.env`:

```env
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=energy_db
MYSQL_USER=energy_user
MYSQL_PASSWORD=energy123
```

### 4. Build dos Serviços

#### Opção A: Build Individual (Maven)

```bash
# User Service
cd user-service
mvn clean package -DskipTests
cd ..

# Device Service
cd device-service
mvn clean package -DskipTests
cd ..

# Ingestion Service
cd ingestion-service
mvn clean package -DskipTests
cd ..
```

#### Opção B: Build via Docker Compose

```bash
docker-compose build
```

---

## 🚀 Executando o Projeto

### Iniciar Todos os Serviços

```bash
docker-compose up -d
```

### Verificar Status dos Containers

```bash
docker-compose ps
```

Saída esperada:
```
NAME                    STATUS              PORTS
mysql-home-energy       Up (healthy)        0.0.0.0:3307->3306/tcp
zookeeper-home-energy   Up                  0.0.0.0:2181->2181/tcp
kafka-home-energy       Up                  0.0.0.0:9092->9092/tcp
kafka-ui-home-energy    Up                  0.0.0.0:8070->8080/tcp
ingestion-service       Up                  0.0.0.0:8082->8082/tcp
```

### Logs dos Serviços

```bash
# Todos os serviços
docker-compose logs -f

# Serviço específico
docker-compose logs -f ingestion-service
docker-compose logs -f kafka
```

### Parar os Serviços

```bash
docker-compose down

# Com remoção de volumes (dados serão perdidos)
docker-compose down -v
```

---

## 📡 API Endpoints

### User Service (http://localhost:8080)

#### Criar Usuário
```bash
POST /api/v1/user
Content-Type: application/json

{
  "name": "João",
  "surname": "Silva",
  "email": "joao@email.com",
  "address": "Rua A, 123",
  "alerting": true,
  "energyAlertingThreshold": 100.0
}
```

#### Buscar Usuário
```bash
GET /api/v1/user/{id}
```

#### Atualizar Usuário
```bash
PUT /api/v1/user
Content-Type: application/json

{
  "id": 1,
  "name": "João",
  "surname": "Silva Santos",
  "email": "joao.novo@email.com",
  "address": "Rua B, 456",
  "alerting": false,
  "energyAlertingThreshold": 150.0
}
```

#### Deletar Usuário
```bash
DELETE /api/v1/user/{id}
```

---

### Device Service (http://localhost:8081)

#### Criar Dispositivo
```bash
POST /api/v1/device/create
Content-Type: application/json

{
  "name": "Smart Lamp Sala",
  "type": "LIGHT",
  "location": "Sala de Estar",
  "userId": 1
}
```

**Tipos de dispositivos suportados:**
- `SPEAKER`
- `CAMERA`
- `THERMOSTAT`
- `LOCK`
- `LIGHT`
- `DOORBELL`

#### Buscar Dispositivo
```bash
GET /api/v1/device/{id}
```

#### Atualizar Dispositivo
```bash
PUT /api/v1/device/{id}
Content-Type: application/json

{
  "name": "Smart Lamp Quarto",
  "type": "LIGHT",
  "location": "Quarto Principal",
  "userId": 1
}
```

#### Deletar Dispositivo
```bash
DELETE /api/v1/device/{id}
```

---

### Ingestion Service (http://localhost:8082)

#### Ingerir Dados de Consumo
```bash
POST /api/v1/ingestion
Content-Type: application/json

{
  "deviceId": 1,
  "energyConsumed": 1.45,
  "timestamp": "2025-12-11T14:30:00Z"
}
```

**Resposta:** `201 Created` (sem corpo)

---

## 🎲 Simulação de Dados

O **Ingestion Service** possui dois simuladores de carga integrados:

### 1. Simulador Contínuo (ContinuosDataSimulator)

- **Descrição:** Envia requisições sequenciais em intervalos fixos
- **Configuração:** `application.properties`

```properties
simulation.request-internal=3
simulation.interval-ms=5000
simulation.ingestion-endpoint=http://localhost:8082/api/v1/ingestion
```

**Comportamento:**
- Envia 3 requisições a cada 5 segundos
- Dispositivos aleatórios (ID 1-5)
- Consumo entre 0.0 e 2.0 kWh

---

### 2. Simulador Paralelo (ParallelDataSimulation)

- **Descrição:** Envia requisições paralelas usando thread pool
- **Configuração:** `application.properties`

```properties
simulation.parallel-threads=10
simulation.requests-per-interval=5000
simulation.endpoint=http://localhost:8082/api/v1/ingestion
simulation.execution-interval-ms=5000
```

**Comportamento:**
- 10 threads paralelas
- 5000 requisições por execução (500 por thread)
- Executa a cada 5 segundos
- Dispositivos aleatórios (ID 1-1000)
- Consumo entre 0.1 e 5.0 kWh

**⚠️ Atenção:** O simulador paralelo gera **carga significativa**. Ajuste os parâmetros conforme capacidade do sistema.

---

### Desabilitar Simuladores

Para desabilitar, comente a anotação `@Component`:

```java
// @Component
@Slf4j
public class ParallelDataSimulation implements CommandLineRunner {
    // ...
}
```

---

## 🗄️ Estrutura do Banco de Dados

### Tabela: `user`

| Coluna                     | Tipo         | Descrição                          |
|----------------------------|--------------|------------------------------------|
| id                         | BIGINT       | PK, Auto Increment                 |
| name                       | VARCHAR(100) | Nome do usuário                    |
| surname                    | VARCHAR(255) | Sobrenome                          |
| email                      | VARCHAR(255) | Email único                        |
| address                    | TEXT         | Endereço residencial               |
| alerting                   | TINYINT(1)   | Flag de alertas ativos             |
| energy_alerting_threshold  | DOUBLE       | Limite de consumo para alerta (kWh)|

**Constraints:**
- Unique Key: `uk_user_email (email)`

---

### Tabela: `device`

| Coluna    | Tipo         | Descrição                          |
|-----------|--------------|------------------------------------|
| id        | BIGINT       | PK, Auto Increment                 |
| name      | VARCHAR(255) | Nome do dispositivo                |
| type      | VARCHAR(50)  | Tipo do dispositivo (enum)         |
| location  | VARCHAR(255) | Localização física                 |
| user_id   | BIGINT       | FK para user.id                    |

**Constraints:**
- Foreign Key: `fk_device_user (user_id)` → `user(id)` ON DELETE CASCADE
- Index: `idx_device_user_id (user_id)`

---

### Migrações Flyway

Localizadas em: `src/main/resources/db/migration/`

```
V1__create_user_table.sql
V2__create_device_table.sql
```

**Aplicação automática:** As migrações são executadas na inicialização dos serviços.

---

## 📊 Monitoramento

### Kafka UI

Acesse: **http://localhost:8070**

**Recursos:**
- Visualização de tópicos e mensagens
- Monitoramento de consumer groups
- Inspeção de offsets
- Estatísticas de throughput

**Tópico principal:** `energy-usage`

---

### Logs de Performance (AOP)

O **User Service** possui aspectos para logging automático:

#### ExecutionTimeAspect
Registra tempo de execução dos controllers:

```
Controller method UserController.createUser(..) executed in 145 ms
```

#### LoggingAspect
Registra entrada/saída dos métodos de serviço:

```
→ Calling service method: UserService.createUser(..) with arguments: [UserDto(...)]
← Service method: UserService.createUser(..) returned: UserDto(id=1, ...)
✗ Service method: UserService.getUserById(..) threw exception: User not found
```

---

### Healthcheck do MySQL

```bash
docker exec mysql-home-energy mysqladmin ping -h localhost -u root -prootpassword
```

Resposta esperada: `mysqld is alive`

---

### Verificar Conectividade do Kafka

```bash
# Listar tópicos
docker exec kafka-home-energy kafka-topics --list --bootstrap-server localhost:29092

# Descrever tópico
docker exec kafka-home-energy kafka-topics --describe --topic energy-usage --bootstrap-server localhost:29092
```

---

## 🔧 Troubleshooting

### Problema: Container do Kafka não inicia

**Sintoma:**
```
kafka-home-energy | Error: Network is unreachable
```

**Solução:**
1. Verifique o IP do host no `docker-compose.yml`:
```yaml
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,EXTERNAL://192.168.1.89:9092
```

2. Substitua `192.168.1.89` pelo IP da sua máquina:
```bash
ip addr show | grep "inet 192"
```

3. Reinicie os containers:
```bash
docker-compose down
docker-compose up -d
```

---

### Problema: Migrações Flyway falham

**Sintoma:**
```
FlywayException: Validate failed: Migration checksum mismatch
```

**Solução:**
1. Limpar histórico do Flyway:
```sql
DELETE FROM flyway_schema_history;
```

2. Recriar banco de dados:
```bash
docker-compose down -v
docker-compose up -d db
```

3. Reiniciar serviços:
```bash
docker-compose up -d
```

---

### Problema: "Too many connections" no MySQL

**Sintoma:**
```
SQLNonTransientConnectionException: Too many connections
```

**Solução:**
1. Ajustar pool de conexões no `application.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
```

2. Aumentar limite do MySQL (temporário):
```sql
SET GLOBAL max_connections = 200;
```

---

### Problema: Ingestion Service não publica no Kafka

**Sintoma:**
```
KafkaException: Failed to send message
```

**Verificações:**
1. Confirmar que Kafka está rodando:
```bash
docker-compose ps kafka
```

2. Testar conectividade:
```bash
telnet localhost 9092
```

3. Verificar logs do Kafka:
```bash
docker-compose logs kafka | grep ERROR
```

4. Validar configuração do bootstrap server:
```properties
spring.kafka.bootstrap-servers=192.168.1.89:9092
```

---

### Problema: Porta já em uso

**Sintoma:**
```
Error starting userland proxy: listen tcp 0.0.0.0:8080: bind: address already in use
```

**Solução:**
1. Identificar processo:
```bash
lsof -i :8080
```

2. Matar processo:
```bash
kill -9 <PID>
```

3. Ou alterar porta no `application.properties`:
```properties
server.port=8090
```

---

## 🔐 Segurança

### Considerações de Produção

⚠️ **Este projeto é para fins educacionais/desenvolvimento.** Para produção, considere:

1. **Autenticação/Autorização:**
   - Implementar Spring Security
   - OAuth2/JWT para APIs
   - Rate limiting

2. **Secrets Management:**
   - Usar variáveis de ambiente seguras
   - AWS Secrets Manager / Vault
   - Nunca comitar credenciais no código

3. **Network Security:**
   - TLS/SSL para comunicação externa
   - Kafka com SASL/SCRAM
   - Firewall rules

4. **Validação de Entrada:**
   - Bean Validation (`@Valid`)
   - Sanitização de inputs
   - Proteção contra SQL Injection (JPA/Hibernate já mitiga)

---

## 📈 Próximos Passos

- [ ] Implementar Consumer Kafka para processar eventos
- [ ] Adicionar agregação de dados por período (hourly/daily)
- [ ] Dashboard de visualização (Grafana)
- [ ] Notificações em tempo real (WebSocket)
- [ ] Testes unitários e de integração
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] API Gateway (Spring Cloud Gateway)
- [ ] Service Discovery (Eureka)

---

## 📝 Licença

Este projeto é open source e está disponível sob a [MIT License](LICENSE).

---

## 👥 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch de feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

****
