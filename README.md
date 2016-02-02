### Requisitos de auto nível

- Sem restrições de arquitetura e tecnologias, com excessão da linguagem Java.
- Documentação e testes
- Requisitos não-funcionais

### Requisitos Funcionais

- Webservice API
    - Carregamento de mapa
    - Consultar menor valor de entrega

### Requisitos Não-Funcionais
    
- O Exemplo com 6 arestas e um total de 5 nós. Porém deve ser considerado "Malhas beeemm mais complexas"
- Carregamento
    - Alta carga de entrada:
        - Quantidade ou ordem de grandeza indefinida mas pelo exemplo dado podemos inferir que podemos ter até 26 nós [A-Z]. Neste cenário o número máximo de arestas possíveis é 650
        - Tempo de resposta esperado indefinido.
            - Sendo uma operação que deve ser feita para implantação do sistema, é aceitável que não seja uma resposta instantânea, porém o carregamento não deve afetar a utilização do serviço por outros usuários.
- Consulta de menor valor de entrega:
    - Carga da entrada e saída de dados constante. Indenpendente do tamanho da malha. Porém o tempo de processamento proporcional ao tamanho do mapa.

### Considerações

- A estrutura de representação interna será um grafo devido a natureza do problema. Os pontos da malha serão representados pelos nós e as rotas serão representadas pelas arestas. 
- Apesar de não especificado, o grafo será considerado do tipo bidirecional. Considerando que rotas de caminhões possuem duas mãos.
- A solução irá se apoiar em ferramentas e frameworks reconhecidos em suas áreas de atuação. Assim a solução "herdará" os pontos fortes destas ferramentas.
- Será usado Dijkstra para busca de melhor rota.

### Decisões arquiteturais:

- O sistema se beneficiará de uma base de dados para grafos, devido as estruturas internas e otimizações específicas para este domínio de problema. Será usado o Neo4J, o banco de dados para grafos de maior utilização e com um nível de maturidade adequado para uso profissional. Ele possui funções nativas de algoritmos para grafos, incluindo Dijkstra.
- O WebService usará a tecnologia REST, que está se tornando a forma de comunicação "de facto" dos serviços web. Como não existe o conhecimento dos consumidores deste serviço foi definido o método de comunicação mais flexível e adotado.
- A mesma lógica foi usada para a escola do JSON como o formato de dados usado no trafego de informações. A única excessão é o endpoint para carregamento de mapa, onde ele deve acetar a entrada como definido no documento de especificação.
- Dropwizard será usado desenvolvimento do web service com API REST. Este framework provê as ferramentas para desenvolvimento seguro de APIs web utilizando os padrões atuais do mercado com segurança e confiabilidade.

### Visão Geral da Arquitetura

- *Service:* Servidor web que expõe uma API REST e se encarrega de receber e responder as requisições de carregamento e consulta.
    - Modulo construído a partir do Framework Dropwizard.
- *Graph:* Módulo encarregado de gerênciar as malhas e realizar busca de trajetos.
    - Os serviços do módulo serão disponibilizados através de uma subclass de da classe abstrata AbstractGraphService.Esta classe é um ponto de extensão do módulo, permitindo a implementação de outras estratégias de armazenamento e busca do grafo. A estratégia atual está disponível na classe Neo4JGraphService, que utiliza o banco de dados de grafos Neo4J para realizar suas funções.
- *Tests:* Framework de testes automáticos utilizando JUnit.

### Web API

#### Criar novo mapa de nome `{name}` com malha logística.
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

#### Retorna o menor valor de entrega e seu caminho.
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
    - 200 - Menor rota calculada
    - 404 - Caminho não pode ser encontrado.

```
GET /{name}/query_route?start={start}&end={end}&auto={auto}&fuel={fuel}
```
Resultado:
```
{
  "route": "A B D",
  "cost": "6.25"
}
```

### Ambiente de desenvolvimento:
- Eclipse Mars.1 Release (4.5.1)
- Maven 3.3.3

