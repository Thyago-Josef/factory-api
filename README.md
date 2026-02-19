# Factory Manager - Sistema de Gestão de Produção Industrial

Sistema completo para controle de estoque de matérias-primas e otimização de produção industrial, desenvolvido como solução para o teste prático da **Autoflex**.

## 📋 Sobre o Projeto

Uma indústria que produz produtos diversos necessita controlar o estoque dos insumos (matérias-primas) necessárias para a produção dos itens que fabrica. Este sistema permite:

- ✅ Manter cadastro completo de produtos e matérias-primas
- ✅ Associar matérias-primas aos produtos com quantidades necessárias
- ✅ Calcular sugestões de produção baseadas no estoque disponível
- ✅ Priorizar produtos de maior valor para otimização de lucro
- ✅ Executar ordens de produção com baixa automática de estoque

---

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 21** - Versão LTS mais recente
- **Quarkus 3.31.3** - Framework supersônico e subatômico
- **Quarkus REST** - API REST moderna e reativa
- **Hibernate ORM with Panache** - Persistência JPA simplificada
- **Jackson** - Processamento JSON
- **Oracle Database Free** - Banco de dados enterprise
- **Flyway** - Migrações e versionamento de banco de dados
- **Hibernate Validator** - Validação de beans (JSR 380)
- **Lombok** - Redução de código boilerplate

### API & Documentação
- **SmallRye OpenAPI** - Especificação OpenAPI 3.0
- **Swagger UI** - Documentação interativa da API

### Testes
- **JUnit 5** - Framework de testes unitários
- **REST Assured** - Biblioteca para testes de API REST
- **H2 Database** - Banco de dados em memória para testes

### Infraestrutura
- **Docker Compose** - Orquestração de containers
- **Maven** - Automação de build e gerenciamento de dependências

---

## 🚀 Como Executar

### Pré-requisitos

Certifique-se de ter instalado:
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (obrigatório)
- [Java 21+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)

### Passo a Passo

#### 1. Clone o Repositório
```bash
git clone https://github.com/Thyago-Josef/factory-api.git
cd factory-api/
```

#### 2. Inicie o Banco de Dados
```bash
docker-compose up -d
```

Este comando irá:
- Baixar a imagem do Oracle Database Free (se necessário)
- Criar e iniciar o container `factory-oracle`
- Configurar o banco na porta `1521`
- Criar o volume persistente para os dados

**⏳ Aguarde ~2 minutos** para o Oracle inicializar completamente.

Acompanhe os logs:
```bash
docker logs -f factory-oracle
```

Quando ver a mensagem **"DATABASE IS READY TO USE!"**, prossiga para o próximo passo.

#### 3. Execute o Backend
```bash
./mvnw quarkus:dev
```

O servidor estará disponível em:
- **API REST:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/q/swagger-ui

#### 4. (Opcional) Execute o Frontend
```bash
cd ../frontend
npm install
npm run dev
```

Frontend disponível em: http://localhost:5173

---

## 🧪 Executar Testes

### Testes Unitários

Os testes utilizam **H2 em memória**, portanto **não é necessário** ter o Oracle rodando:
```bash
./mvnw test
```



---

## 📚 Endpoints da API

### Produtos (Products)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/products` | Lista todos os produtos |
| GET | `/products/{id}` | Busca produto por ID |
| POST | `/products` | Cria novo produto |
| PUT | `/products/{id}` | Atualiza produto |
| DELETE | `/products/{id}` | Remove produto |

### Matérias-Primas (Raw Materials)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/materials` | Lista todas as matérias-primas |
| GET | `/materials/{id}` | Busca matéria-prima por ID |
| POST | `/materials` | Cria nova matéria-prima |
| PUT | `/materials/{id}` | Atualiza matéria-prima |
| DELETE | `/materials/{id}` | Remove matéria-prima |

### Produção (Production)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/production/suggestions` | Calcula sugestões de produção |
| POST | `/production/execute/{productId}?quantity={qty}` | Executa ordem de produção |

**Exemplos de Request:**
```bash
# Sugestões de produção
curl http://localhost:8080/production/suggestions

# Executar produção de 10 unidades do produto ID 1
curl -X POST "http://localhost:8080/production/execute/1?quantity=10"
```

---

## 🐳 Comandos Docker Úteis
```bash
# Ver logs do Oracle
docker logs -f factory-oracle

# Parar o banco
docker-compose down

# Parar e remover volumes (⚠️ apaga todos os dados)
docker-compose down -v

# Reiniciar o banco
docker-compose restart

# Acessar SQL*Plus
docker exec -it factory-oracle sqlplus system/oracle@XEPDB1

# Verificar status
docker ps
```

---

---





## 🏗️ Estrutura do Projeto
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/factory/
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── resource/        # Controllers REST
│   │   │   ├── service/         # Lógica de negócio
│   │   │   └── repository/      # Repositórios Panache
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/factory/
│           ├
│           └── service/         # Testes de serviço
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🎯 Requisitos Atendidos

### Requisitos Não Funcionais
- [x] **RNF001** - Plataforma WEB compatível com Chrome, Firefox, Edge
- [x] **RNF002** - Arquitetura API (backend separado do frontend)
- [x] **RNF003** - Interface responsiva (mobile-first)
- [x] **RNF004** - Persistência em SGBD (Oracle Database Free)
- [x] **RNF005** - Backend desenvolvido em Quarkus
- [x] **RNF006** - Frontend desenvolvido em React + Redux
- [x] **RNF007** - Código em inglês (classes, métodos, tabelas)

### Requisitos Funcionais
- [x] **RF001** - CRUD de produtos no backend
- [x] **RF002** - CRUD de matérias-primas no backend
- [x] **RF003** - Associação de matérias-primas aos produtos
- [x] **RF004** - Consulta de produtos possíveis de produzir
- [x] **RF005** - Interface gráfica para CRUD de produtos
- [x] **RF006** - Interface gráfica para CRUD de matérias-primas
- [x] **RF007** - Interface para associar matérias-primas (integrada ao cadastro)
- [x] **RF008** - Interface para listar sugestões de produção

### Diferenciais Implementados
- [x] Testes unitários (JUnit 5 + REST Assured)
- [x] Priorização por maior valor de produto
- [x] Cálculo automático de viabilidade de produção
- [x] Baixa automática de estoque após produção
- [x] Interface moderna e intuitiva
- [x] Dockerização completa
- [x] Documentação OpenAPI/Swagger
- [x] Migrações de banco com Flyway

---

## 🐛 Solução de Problemas

### Erro: "Port 1521 already in use"

Outro processo está usando a porta 1521:
```bash
# Descubra qual processo
lsof -i :1521  # Mac/Linux
netstat -ano | findstr :1521  # Windows

# Ou mude a porta no docker-compose.yml
ports:
  - "1522:1521"
```

### Erro: "ORA-12514: TNS:listener does not currently know of service"

O Oracle ainda está inicializando. Aguarde 2-3 minutos e tente novamente.

### Erro: "Connection refused"

1. Verifique se o container está rodando:
```bash
docker ps
```

2. Verifique os logs:
```bash
docker logs factory-oracle
```

3. Reinicie o container:
```bash
docker-compose restart
```

### Testes Falhando

Os testes usam H2 em memória, independente do Oracle. Se falharem:
```bash
# Limpe o cache e rode novamente
./mvnw clean test
```

---

## 📖 Documentação Adicional

- [Quarkus Guides](https://quarkus.io/guides/)
- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [REST with Quarkus](https://quarkus.io/guides/rest)
- [Testing Quarkus Applications](https://quarkus.io/guides/getting-started-testing)

---

## 👨‍💻 Autor

Desenvolvido como solução para o teste prático da **Autoflex**.

**GitHub:** [Thyago-Josef](https://github.com/Thyago-Josef)  
**LinkedIn:** [thyagojosenascimento](https://linkedin.com/in/thyagojosenascimento/)

---

## 📄 Licença

Este projeto foi desenvolvido para fins de avaliação técnica.