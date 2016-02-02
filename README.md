- Endpoints:
    - POST /map/{name} - Cria novo mapa com malha logística passada.
```
A B 10
B D 15
A C 20
C D 30
B E 50
D E 30
```
        - 201 - Mapa criado
        - 400 - Parâmetros incorretos passados.
    - GET /path/ - Retorna o menor valor de entrega e seu caminho.
        - 200 - requisição completada
        - 400 - Parâmetros incorretos passados.
        - 404 - Caminho não pode ser encontrado - Ver código de resultado para mais informações:
            - 100: Mapa não existe
            - 101: Nó de origem não existe
            - 102: Nó de destino não existe
            - 103: Caminho entre nós não pode ser encontrado
- Ambiente de desenvolvimento:
    - Eclipse Mars.1 Release (4.5.1)
    - Maven 3.3.3
    -
- Requisitos Não-Funcionais
    - Exemplo com 6 arestas e um total de 5 nós
    - "Malhas beeemm mais complexas"
    - Carregamento
        - Alta carga de entrada:
            - Quantidade ou ordem de grandeza indefinida.
            - Pelo exemplo dado podemos inferir que podemos ter até 26 nós [A-Z]
            - O número máximo de arestas possíveis é 650
            - Tempo de resposta esperado indefinido.
                - Sendo uma operação que deve ser feita para implantação do sistema, é aceitável que não seja uma resposta instantânea, porém o carregamento não deve afetar a utilização do serviço por outros usuários.
    - Consulta de menor valor de entrega:
        - Entrada e saída de dados constante
        - Tempo de processamento proporcional ao tamanho do mapa.
- Melhorias:
