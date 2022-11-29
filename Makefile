.PHONY:

# ==============================================================================
# Docker

local:
	@echo Starting local docker compose
	docker-compose -f docker-compose.local.yaml up -d --build

develop:
	mvn clean package -Dmaven.test.skip
	@echo Starting docker compose
	docker-compose -f docker-compose.yaml up -d --build


# ==============================================================================
# Docker and k8s support grafana - prom-operator

FILES := $(shell docker ps -aq)

down-local:
	docker stop $(FILES)
	docker rm $(FILES)

clean:
	docker system prune -f

logs-local:
	docker logs -f $(FILES)


upload:
	mvn clean package -Dmaven.test.skip
	docker build -t alexanderbryksin/spring_webflux_grpc_microservice:latest --platform=linux/arm64 -f ./Dockerfile .
	docker push alexanderbryksin/spring_webflux_grpc_microservice:latest


k8s_apply:
	kubectl apply -f k8s/microservice/templates

k8s_delete:
	kubectl delete -f k8s/microservice/templates

helm_install:
	kubens default
	helm install -f k8s/microservice/values.yaml microservices k8s/microservice

helm_uninstall:
	kubens default
	helm uninstall microservices

helm_install_all:
	helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
	helm repo update
	kubectl create namespace monitoring
	helm install monitoring prometheus-community/kube-prometheus-stack -n monitoring
	kubens default
	helm install -f k8s/microservice/values.yaml microservices k8s/microservice

helm_uninstall_all:
	kubens monitoring
	helm uninstall monitoring
	kubens default
	helm uninstall microservices
	kubectl delete namespace monitoring