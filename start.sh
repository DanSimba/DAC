#!/bin/bash

echo " = Executando docker = "

docker compose up --build -d

if [ $? -eq 0 ]; then 
	echo "Executado com sucesso"
	echo ""
	echo "==================================="
	docker ps -a
	echo "==================================="
	echo "informações sobre como docker subiu"
	echo "==================================="	
else 
	echo "Deu algum xabu rapaziada" >&2
	exit 1
fi