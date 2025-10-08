infra/start:
	docker compose --file .docker/docker-compose.yml up -d

infra/stop:
	docker compose --file .docker/docker-compose.yml down

infra/restart: infra/stop infra/start

kafka/read-product-topic:
	docker compose --file .docker/docker-compose.yml exec -it kafka \
 	/opt/kafka/bin/kafka-console-consumer.sh \
      --bootstrap-server localhost:9092 \
      --topic product \
      --from-beginning \
      --property print.timestamp=true \
      --property print.key=true \
      --property print.value=true \
      --property print.partition=true \
      --property print.offset=true

kafka/reset-stream-product-redis:
	docker compose --file .docker/docker-compose.yml exec -it kafka \
	/opt/kafka/bin/kafka-streams-application-reset.sh \
      --application-id redis-aggregator \
      --input-topics product \
      --to-earliest \
      --force

kafka/delete-product-topic:
	docker compose --file .docker/docker-compose.yml exec -it kafka \
	/opt/kafka/bin/kafka-topics.sh \
		--bootstrap-server localhost:9092 \
		--delete \
		--topic product
