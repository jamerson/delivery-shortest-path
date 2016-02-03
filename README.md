### Requisitos Funcionais

- Webservice API
    - Carregamento de mapa
        - Informações devem ser persistidas para consultas subsequentes
    - Consultar menor valor de entrega

### Requisitos Não-Funcionais
    
- O Exemplo a seguir, usado como referência, contém 6 arestas e um total de 5 nós. Mas de acordo com a especificação, o sistema deve comportar um cenário bem mais complexo.
```
A B 10
B D 15
A C 20
C D 30
B E 50
D E 30
```

- Carregamento
    - Alta carga de entrada:
        - Quantidade ou ordem de grandeza indefinida mas pelo exemplo dado podemos inferir que podemos ter até 26 nós [A-Z]. Neste cenário o número máximo de arestas possíveis é 650.
        - Tempo de resposta esperado indefinido.
            - Sendo uma operação que deve ser feita para implantação do sistema, é aceitável que não seja uma resposta instantânea, porém o carregamento não deve afetar a utilização do serviço por outros usuários.
- Consulta de menor valor de entrega:
    - Carga da entrada e saída de dados constante. Indenpendente do tamanho da malha. Porém o tempo de processamento proporcional ao tamanho do mapa.

### Considerações

- A estrutura de representação interna será um grafo devido a natureza do problema. Os pontos da malha serão representados pelos nós e as rotas serão representadas pelas arestas. 
- Apesar de não especificado, o grafo adotado será simples não-completo e bidirecional (Considerando que rotas de caminhões possuem duas mãos).
- Ferramentas e frameworks reconhecidos em suas áreas de atuação serão usados com objetivo de aproveitar os seus pontos fortes destas ferramentas.
- Será usado Dijkstra para busca de melhor rota considerando que este é um algoritmo robusto com tempo de execuções bons frente a grande massa de dados.

### Decisões Arquiteturais

- O sistema se beneficiará de uma base de dados para grafos, devido as estruturas internas e otimizações específicas para este domínio de problema. Com isto em mente, O Neo4J foi escolhido pois é o banco de dados para grafos de maior utilização e com um nível de maturidade adequado para uso profissional. Ele possui funções nativas de algoritmos para grafos, incluindo Dijkstra.
- O WebService usará a tecnologia REST, que está se tornando a forma de comunicação "de facto" dos serviços web. Como não temos o conhecimento dos consumidores deste serviço este formato vem a calhar por ser o método de comunicação mais flexível e amplamente adotado.
- A mesma lógica foi usada para a escola do JSON como o formato de dados usado no tráfego de informações. A única excessão é o endpoint para carregamento de mapa, onde ele deve aceitar a entrada como definido no documento de especificação.
- Dropwizard será usado no desenvolvimento do web service com API REST. Este framework provê as ferramentas para um desenvolvimento seguro de APIs web utilizando os padrões atuais do mercado com segurança e confiabilidade.

### Visão Geral da Arquitetura

#### Visão Estrutural

![Visão estrutural da Arquitetura](https://github.com/jamerson/delivery-shortest-path/blob/master/extras/arch.png)

A arquitetura pode ser dividida nos seguintes módulos:
- *Dropwizard*: Framework com servidor web e bibliotecas de validação, tratamento de JSON, log de erros e tratamento de requisições web. Seus sub-módulos principais são:
    - *Service*: Módulo com código referente a definição de endpoints disponibilizados via API e ele é responsável pela comunicação com o módulo *Graph*. Seu sub-módulo *Resources* Agrupam os endpoints em resources REST. 
- *Graph*: Módulo encarregado de gerênciar as malhas e de realizar busca de trajetos.
    - *GraphService*: Os serviços do módulo serão disponibilizados através de uma subclass da classe abstrata AbstractGraphService. Esta classe é um ponto de extensão do módulo, permitindo a implementação de outras estratégias de armazenamento e busca do grafo. O serviço real é disponibilizado através de uma abstract factory chamada *GraphServiceFactory*, além disso ela é responsável por criar e manter o singleton do serviço real do grafo.
    - *Neo4jGraphService*: A estratégia atual está disponível nesta classe, que utiliza o banco de dados de grafos Neo4J para realizar suas funções.

Durante a execução do serviço, o servidor web será encarregado de receber e criar threads para as requisições. Cada requisição irá validar os dados de entrada e solicitar a instância do serviço de grafo para a factory. Como o serviço é um singleton, existe apenas uma instância do serviço para todas as requisições(os acessos aos recursos compartilhados do driver do grafo são envolvidos em transações).

### Web API

#### Criar novo mapa de nome `{name}` com malha logística.
A requisição do tipo POST deve ter o nome do mapa em sua URL e em seu corpo a definição da malha deste mapa. A definição e uma sequência de linhas onde cada linha define uma rota no formato `<ponto inicial> <ponto final> <custo>`. O `<ponto inicial>` e o `<ponto final>` são Strings compostas por letras maiúsculas do alfabeto e o `<custo>` é um número do tipo double. Linhas com rotas já definidas serão descartadas. 

- Método: `POST`
- Entrada:
    - Parâmetros Path:
        - `name`: Nome da malha.
    - Body:
        linhas no formato: `A B 10` descrevendo a malha.
    - Content-Type: text
- Respostas:
    - 201 - Mapa criado
    - 400 - Parâmetros incorretos.
- Exemplo:
```
POST /map/{name}
A B 10
B D 15
A C 20
C D 30
B E 50
D E 30
```

#### Retornar o menor valor de entrega e seu caminho usando a malha `{name}`.
A requisição do tipo GET deve ter o nome do mapa em sua URL e as parametros definidos abaixo. `start` e `end` devem ser pontos válidos no mapa em questão. `auto` e `fuel` devem ser algum número do tipo double maior que zero. A resposta será um JSON válido com `route` contendo uma String com todos os pontos da rota e `cost` com o valor do custo da rota.
- Método: `GET`
- Entrada:
    - Parametros Path:
        - `name`: Nome da malha.
    - Parâmetros Query:
     - `start`: Ponto inicial
     - `end`: Ponto final
     - `auto`: Autonomia do caminhão
     - `fuel`: Valor do litro de combustível
    - Content-Type: text
- Respostas:
    - 400 - Parâmetros incorretos.
    - 200 - Menor rota calculada.
    - 404 - Caminho não pode ser encontrado.
- Exemplo:

```
GET /{name}/query_route?start={start}&end={end}&auto={auto}&fuel={fuel}
```

- Resultado:

```json
{
  "route": "A B D",
  "cost": "6.25"
}
```

### Ambiente de Desenvolvimento
- Eclipse Mars.1 Release (4.5.1)
- Maven 3.3.3

#### Execução do serviço na porta 8080
```
$ mvn clean package
$ java -jar target/delivery-shortest-path-1.0.0.jar server
```
- Uma interface de administração etá disponível na porta 8081 com o registro de tempo de execução de cada chamada a API.

#### Execução dos testes unitários
```
$ mvn clean test
```

### Referências

- [Dropwizard](http://www.dropwizard.io/0.9.2/docs/getting-started.html#getting-started)
- [Neo4j](https://github.com/neo4j/neo4j)
- [Algorítimo Dijkstra](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)
