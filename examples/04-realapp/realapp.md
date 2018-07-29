curl -H "Content-Type: application/json" -d '{"id": "1", "name": "tonda"}' http://localhost:9080/api/users

docker exec es-kafka kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic user

docker exec es-kafka kafka-streams-application-reset --application-id realapp --input-topics price,user \
--bootstrap-servers localhost:9092 --dry-run