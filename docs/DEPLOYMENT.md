# Deployment Guide

## Local Development

### Prerequisites
- Docker and Docker Compose
- Java 21
- Node.js 20+
- Python 3.12+

### Docker Compose (Recommended)

```bash
# Start everything
docker compose up -d

# Check services
docker compose ps

# View logs
docker compose logs -f backend

# Stop
docker compose down
```

### Local Development Setup

#### Backend
```bash
cd Backend
# Start infrastructure
docker compose -f ../docker-compose.yml up postgres redis kafka -d

# Build and run
gradle bootRun
```

#### Frontend
```bash
cd Frontend
npm install
npm run dev
```

#### AI Backend
```bash
cd AI-Backend
python -m venv venv
source venv/bin/activate  # Linux/Mac
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

## Production Deployment

### Kubernetes

```bash
# Apply all manifests
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/secrets.yaml
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/backend/
kubectl apply -f kubernetes/frontend/
kubectl apply -f kubernetes/ai-backend/
kubectl apply -f kubernetes/ingress.yaml
kubectl apply -f kubernetes/hpa.yaml

# Check status
kubectl get pods -n taskflow
kubectl get ingress -n taskflow
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `AI_PROVIDER` | `ollama` | AI provider to use |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama server URL |
| `OLLAMA_MODEL` | `llama3.1` | Model to use with Ollama |
| `JWT_SECRET` | (required) | Secret for JWT signing |
| `JWT_EXPIRY` | `900000` | Access token expiry (ms) |
| `JWT_REFRESH_EXPIRY` | `604800000` | Refresh token expiry (ms) |

### Health Checks

```bash
# Backend
curl http://localhost:8080/actuator/health

# AI Backend
curl http://localhost:8000/health

# Frontend
curl http://localhost:80
```

## Monitoring

- **Prometheus**: Metrics at `http://localhost:8080/actuator/prometheus`
- **Swagger UI**: API docs at `http://localhost:8080/swagger-ui.html`
- **AI Docs**: AI API docs at `http://localhost:8000/docs`
