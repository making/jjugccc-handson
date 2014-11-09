JJUG CCC Fall 2014 [R5-3] Spring Bootハンズオン～Spring Bootで作るマイクロサービスアーキテクチャ！手順書

.. contents:: 目次
  :depth: 2


事前準備
================================================================================

必要なソフトウェアのインストール
--------------------------------------------------------------------------------

Mac/Windowsユーザー向けに記述しています。Linuxで実施する場合は同等の手順を実施してください。

Java SE 8
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Maven
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

cURL (Windowsの場合)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Git Bash (Windowsの場合)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

jq (オプション)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
必須ではないですが、JSON出力の整形用に\ http://stedolan.github.io/jq/\ をインストールしておくと良いです。

Redis
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Gitbucket
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
https://github.com/takezoe/gitbucket/releases/download/2.5/gitbucket.war\ より、Gitbucketをダウンロードしてください。

IntelliJ IDEA
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

https://www.jetbrains.com/idea/download/\ より、IntelliJ IDEA 14のCommunity EditionまたはUltimate Editionをダウンロードしてインストールしてください。

Spring Tool SuiteやEclipseを使用してもハンズオンを実施できますが、ハンズオンの説明はIntelliJ IDEAを用いて行います。


Mavenリポジトリのコピー
--------------------------------------------------------------------------------
ハンズオンはオフライン環境で行います。

必要なライブラリをインターネットからダウンロードせず直接Mavenリポジトリにコピーします。

(ハンズオン資材のルートフォルダ)/repository以下を(ホームディレクトリ)/.m2/repository以下にコピーしてください。

.. code-block:: bash

    $ cp -r (ハンズオン資材のルートフォルダ)/repository/* ~/.m2/repository/


演習の全体像
================================================================================

本演習で「URL短縮サービス」を題材にマイクロサービスアーキテクチャを構築します。

最終的に構築するアーキテクチャを以下に示します。

.. figure:: ./images/exercise00-01.png
   :width: 80%

演習1ではSpring Bootを用いて単一の「URL短縮サービス」を作成します。

.. figure:: ./images/exercise00-02.png
   :width: 80%

演習2ではSpring Cloud Configを用いて「URL短縮サービス」に動的コンフィギュレーションを追加します。

.. figure:: ./images/exercise00-03.png
   :width: 80%

演習3では「URL短縮サービス」のUIを追加し、Spring Cloud Netflixを用いて「URL短縮サービス」にマイクロサービスアーキテクチャのための様々なパターンを追加します。

.. figure:: ./images/exercise00-04.png
   :width: 80%


演習1 Spring Bootで「URL短縮サービス」を作る
================================================================================

演習1ではマイクロサービス界でのFizzBuzz問題である、「URL短縮サービス」を作ります。

課題1で\ ``ConcurrentHashMap``\ を使った実装。課題2でRedisを使った実装を行います。

インポートするプロジェクトにほとんどのコードが実装されているので、課題で実装するコードはほんの数行です。

演習プロジェクトの作成・インポート
--------------------------------------------------------------------------------

新規プロジェクト作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

IntelliJ IDEAを開いて「New Project」で新規プロジェクトを作成します。
以下のように「Empty Project」を選択してください。

.. figure:: ./images/import-exercise01-01.png
   :width: 80%

以下の設定値を入力してください。\ **デフォルト値から変更するので注意してください** \ 。

.. tabularcolumns:: |p{0.30\linewidth}|p{0.70\linewidth}|
.. list-table::
   :stub-columns: 1
   :widths: 30 70

   * - | Project name
     - | jjugccc-handson
   * - | Project location
     - | (ハンズオン資材のルートフォルダ)/exercise


.. figure:: ./images/import-exercise01-02.png
   :width: 80%

「Project Structure」で以下の設定値を入力してください。

.. tabularcolumns:: |p{0.30\linewidth}|p{0.70\linewidth}|
.. list-table::
   :stub-columns: 1
   :widths: 30 70

   * - | Project SDK
     - | 1.8
   * - | Project language level
     - | 8


.. figure:: ./images/import-exercise01-03.png
   :width: 80%


JDKが未設定の場合は、「New」を押してJDKを設定してください。JAVA_HOMEに相当するフォルダを選択すれば良いです。


.. figure:: ./images/import-exercise01-04.png
   :width: 40%

演習プロジェクトのインポート
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
「File」->「Import Module」で演習プロジェクトをインポートします。

.. figure:: ./images/import-exercise01-05.png
   :width: 80%

「(ハンズオン資材のルートフォルダ)/exercise/01-urlshortener」を選択してください。

.. figure:: ./images/import-exercise01-06.png
   :width: 80%

「Import module from external model」で「Maven」を指定してください。

.. figure:: ./images/import-exercise01-07.png
   :width: 80%

\ **「Search for projects recursively」と「Import Maven projects automatically」にチェックを入れて**\ 、次に進んでください。

.. figure:: ./images/import-exercise01-08.png
   :width: 80%

「Next」を繰り返すと、以下のように演習1用のMavenプロジェクトがインポートされます。


.. figure:: ./images/import-exercise01-09.png
   :width: 80%


課題1 TODOを埋めてプログラムを完成させてください
--------------------------------------------------------------------------------

\ ``demo.UrlShortener``\ を編集してください。

以下\ ``TODO``\ 部分を埋めてください。

.. code-block:: java

    @RequestMapping(value = "/", method = RequestMethod.POST)
    ResponseEntity<String> save(@RequestParam String url) {
        if (urlValidator.isValid(url)) {
            String hash = ""/* TODO (1) URLをハッシュ化。ハッシュアルゴリズムには 32-bit murmur3 algorithm を使用する。 */;
            // ヒント: com.google.common.hash.Hashing.murmur3_32()を使う
            // TODO (2) urlMapにhashに紐づくURLを追加する。
            return new ResponseEntity<>(urlShortenUrl + "/" + hash, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

\ ``UrlShortener``\ クラスを右クリックして、\ ``Run UrlShortener.main()``\ をクリックしてください。

.. figure:: ./images/exercise01-01.png
   :width: 80%


以下のようにも実行できます。

.. code-block:: bash

    $ cd (ハンズオン資材のルートフォルダ)
    $ mvn spring-boot:run -f exercise/01-urlshortener/urlshortener/pom.xml

以下の結果が返るか確認してください。

.. code-block:: bash

    $ curl -X POST http://localhost:8080 -d "url=http://google.com"
    http://localhost:8080/58f3ae21
    $ curl -X GET http://localhost:8080/58f3ae21
    http://google.com


\ ``UrlShortenerTest``\ クラスを右クリックして、\ ``Run UrlShortenerTest``\ をクリックしてください。

.. figure:: ./images/exercise01-02.png
   :width: 80%

テストが成功したら課題1は完了です。

.. figure:: ./images/exercise01-03.png
   :width: 80%

テストは以下のようにも実行できます。

.. code-block:: bash

    $ mvn test -f exercise/01-urlshortener/urlshortener/pom.xml

課題2 Redisを使ってConcurrentHashMap使用部分を書き換えましょう
--------------------------------------------------------------------------------
次に\ ``ConcurrentHashMap``\ の部分をRedisを使用するように書き換えます。
Spring BootによるAutoconfigurationでいかに簡単にRedis (Spring Data Redis)を使用できるか体験します。


Redisの起動
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Macの場合

.. code-block:: bash

    $ cd (ハンズオン資材のルートフォルダ)/software/redis-2.8.17
    $ ./src/redis-server
    [34286] 09 Nov 05:27:07.455 # Warning: no config file specified, using the default config. In order to specify a config file use ./src/redis-server /path/to/redis.conf
    [34286] 09 Nov 05:27:07.457 * Increased maximum number of open files to 10032 (it was originally set to 2560).
                    _._
               _.-``__ ''-._
          _.-``    `.  `_.  ''-._           Redis 2.8.17 (00000000/0) 64 bit
      .-`` .-```.  ```\/    _.,_ ''-._
     (    '      ,       .-`  | `,    )     Running in stand alone mode
     |`-._`-...-` __...-.``-._|'` _.-'|     Port: 6379
     |    `-._   `._    /     _.-'    |     PID: 34286
      `-._    `-._  `-./  _.-'    _.-'
     |`-._`-._    `-.__.-'    _.-'_.-'|
     |    `-._`-._        _.-'_.-'    |           http://redis.io
      `-._    `-._`-.__.-'_.-'    _.-'
     |`-._`-._    `-.__.-'    _.-'_.-'|
     |    `-._`-._        _.-'_.-'    |
      `-._    `-._`-.__.-'_.-'    _.-'
          `-._    `-.__.-'    _.-'
              `-._        _.-'
                  `-.__.-'

    [34286] 09 Nov 05:27:07.465 # Server started, Redis version 2.8.17
    [34286] 09 Nov 05:27:07.466 * DB loaded from disk: 0.001 seconds
    [34286] 09 Nov 05:27:07.466 * The server is now ready to accept connections on port 6379

Windowsの場合、redis-server.exeを実行してください。


ソースコードの修正
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

以下の3カ所を書き換えてください。

.. code-block:: java

    final ConcurrentHashMap<String, String> urlMap = new ConcurrentHashMap<>();
    // ↓
    @Autowired StringRedisTemplate redisTemplate;

に書き換えてください。

.. code-block:: java

    urlMap.putIfAbsent(hash, url);
    // ↓
    redisTemplate.opsForValue().set(hash, url);

に書き換えてください。


.. code-block:: java

    String url = urlMap.get(hash);
    // ↓
    String url = redisTemplate.opsForValue().get(hash);

に書き換えてください。


書き換えた後に、課題1同様にテストが通れば課題2も完了です。

起動したアプリケーションは終了しておいてください。Redisは起動したままにしてください。

演習2 Spring Cloud Configで動的コンフィギュレーション
================================================================================
演習2ではSpring Cloud Configを使った動的コンフィギュレーションを体験します。


演習2で扱うシステムのアーキテクチャ図を以下に示します。

.. figure:: ./images/exercise02-01.png
   :width: 40%

Config Clientとして演習1で作成した「URL短縮サービス」を使用し、Config Server(作成済み)から設定を取得します。

Config ServerはデフォルトでGithubに接続しますが、今回はオフライン環境で実施するため、ローカルに立ち上げたGitbucketを使用します。

演習プロジェクトのインポート
--------------------------------------------------------------------------------
「File」->「Import Module」で演習プロジェクトをインポートします。
「(ハンズオン資材のルートフォルダ)/exercise/02-distributed-config」を選択してください。

.. figure:: ./images/import-exercise02-01.png
   :width: 80%

.. figure:: ./images/import-exercise02-02.png
   :width: 80%

* configserverはConfig Serverを設定したプロジェクトです。
* urlshortenerは演習1にConfig Clientの依存関係を追加したプロジェクトです。

どちらも既に設定済みで、新規にコーディングする必要はありません。

Gitbucketの起動 & Config Repositoryの作成
--------------------------------------------------------------------------------

Gibucketを起動しましょう。8080番ポートを使用するので、このポートを使用しているアプリがあれば終了してください。

.. code-block:: bash

    $ cd (ハンズオン資材のルートフォルダ)/software
    $ java -jar gitbucket.war

http://localhost:8080\ にアクセスしユーザー名/パスワードともに「root」でログインしてください。

.. figure:: ./images/exercise02-02.png
   :width: 80%

右上のメニューから「New repository」をクリックしてください。

.. figure:: ./images/exercise02-03.png
   :width: 80%

Repository nameに「config-repo」を入力し、「Initialize this repository with a README」にチェックを入れ、「Create repository」をクリックしてください。

.. figure:: ./images/exercise02-04.png
   :width: 80%

これでConfig Respositoryが作成できました。

.. figure:: ./images/exercise02-05.png
   :width: 80%

動作確認用のコンフィギュレーションを作成しましょう。レポジトリ名の右に「+」マークをクリックしてください。

.. figure:: ./images/exercise02-06.png
   :width: 80%


ファイル名を「foo.properties」にし、以下の内容を記入し、「Commit changes」をクリックしてください。

.. code-block:: properties

    foo: 123456
    bar: abcdef

.. figure:: ./images/exercise02-07.png
   :width: 80%

もう一つファイルを作成します。
ファイル名を「foo-development.properties」にし、以下の内容を記入し、「Commit changes」をクリックしてください。

.. code-block:: properties

    foo: Hello!

.. figure:: ./images/exercise02-08.png
   :width: 80%

Config Server起動
--------------------------------------------------------------------------------

「configserver」の\ ``bootstrap.yml``\ に以下の設定が行われていることを確認してください。

.. code-block:: yaml

    spring.cloud.config.server.uri: http://localhost:8080/git/root/config-repo.git

以下のコマンドでConfig Serverを起動起動してください。

.. code-block:: bash

    $ cd (ハンズオン資材のルートフォルダ)/exercise/02-distributed-config
    $ mvn spring-boot:run -f configserver/pom.xml

動作確認しましょう。

.. code-block:: bash

    $ curl http://localhost:8888/admin/env

以下ではjqを使って整形した結果を示します。


.. code-block:: bash

    $ curl http://localhost:8888/admin/env | jq .
    {
      "defaultProperties": {
        "spring.config.name": "configserver"
      },
      "applicationConfig: [classpath:/bootstrap.yml]": {
        "spring.cloud.config.server.uri": "http://localhost:8080/git/root/config-repo.git"
      },
      "applicationConfig: [classpath:/configserver.yml]": {
        "management.context_path": "/admin",
        "spring.application.name": "configserver",
        "server.port": 8888,
        "info.component": "Config Server",
        "spring.jmx.default_domain": "cloud.config.server"
      },
      // ... 省略
    }

\ ``spring.cloud.config.server.uri``\ が反映されていることを確認してください。

次にコンフィギュレーションを取得します。app名はfoo、profile名はdefaultにします。

.. code-block:: bash

    $ curl http://localhost:8888/foo/default

以下ではjqを使って整形した結果を示します。

.. code-block:: bash

    $ curl http://localhost:8888/foo/default | jq .
    {
      "propertySources": [
        {
          "source": {
            "foo": "123456",
            "bar": "abcdef"
          },
          "name": "http://localhost:8080/git/root/config-repo.git/foo.properties"
        }
      ],
      "label": "master",
      "name": "default"
    }

次にprofileを変更して取得しましょう。

.. code-block:: bash

    $ curl http://localhost:8888/foo/development


以下ではjqを使って整形した結果を示します。

.. code-block:: bash

    $ curl http://localhost:8888/foo/development | jq .
    {
      "propertySources": [
        {
          "source": {
            "foo": "Hello!"
          },
          "name": "http://localhost:8080/git/root/config-repo.git/foo-development.properties"
        },
        {
          "source": {
            "foo": "123456",
            "bar": "abcdef"
          },
          "name": "http://localhost:8080/git/root/config-repo.git/foo.properties"
        }
      ],
      "label": "master",
      "name": "development"
    }

\ ``foo-development.properties``\ で上書きしていることが分かります。


「URL短縮サービス」向けのコンフィギュレーション作成
--------------------------------------------------------------------------------

同様に、URL短縮サービス向けのコンフィギュレーションを「urlshortener.yml」に作成します。設定内容は以下の通りです。

.. code-block:: yaml

    urlshorten:
      url: http://localhost:${server.port}
    spring:
      redis:
        host: localhost # server host
        password: # server password
        port: 6379 # connection port
        pool:
          max-idle: 8 # pool settings ...
          min-idle: 0
          max-active: 8
          max-wait: -1
    endpoints.restart:
      enabled: true

.. figure:: ./images/exercise02-09.png
   :width: 80%


動作確認しましょう。(Config Serverの再起動は不要です)

.. code-block:: bash

    $ curl http://localhost:8888/urlshortener/default


以下ではjqを使って整形した結果を示します。

.. code-block:: bash

    $ curl http://localhost:8888/urlshortener/default | jq .
    {
      "propertySources": [
        {
          "source": {
            "spring.redis.pool.max-idle": 8,
            "spring.redis.password": "",
            "spring.redis.host": "localhost",
            "spring.redis.port": 6379,
            "urlshorten.url": "http://localhost:${server.port}",
            "endpoints.restart.enabled": true,
            "spring.redis.pool.max-active": 8,
            "spring.redis.pool.min-idle": 0,
            "spring.redis.pool.max-wait": -1
          },
          "name": "http://localhost:8080/git/root/config-repo.git/urlshortener.yml"
        }
      ],
      "label": "master",
      "name": "default"
    }

Git上の変更が即反映されています。


「URL短縮サービス」(Config Client)の起動
--------------------------------------------------------------------------------

次にConfig Clientとして、「URL短縮サービス」を起動します。

インポートしたプロジェクト(exercise/02-distributed-config/urlshortener)のpom.xmlに以下の依存関係が追加されていることを確認してください。

.. code-block:: xml

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

また、urlshortenerのbootstrap.ymlに

.. code-block:: yaml

    spring:
      application:
        name: urlshortener

が設定されていることを確認してください。

「URL短縮サービス」を起動しましょう。8080番ポートは既に起動しているので、プログラム引数に\ ``--server.port=8081``\ をつけて8081番ポートで起動します。

.. code-block:: bash

    $ cd (ハンズオン資材のルートフォルダ)/exercise/02-distributed-config
    $ mvn spring-boot:run -f urlshortener/pom.xml -Drun.arguments="--server.port=8081"


演習1同様に以下のリクエストを送ってください。(ポート名が変更されていることに気をつけてください)

.. code-block:: bash

    $ curl -X POST http://localhost:8081 -d "url=http://google.com"
    http://localhost:8081/58f3ae21
    $ curl -X GET http://localhost:8081/58f3ae21
    http://google.com

次にConfig Server(urlshortener.yml)の値を変えましょう。

http://localhost:8080/root/config-repo/blob/master/urlshortener.yml\ にアクセスし、「Edit」ボタンをクリックしてください。

.. figure:: ./images/exercise02-10.png
   :width: 80%

\ ``urlshorten.url``\ を\ ``http://localhost:9999``\ に変更して「Commit changes」をクリックしてください。(\ **この設定は演習3で使用します**\ )。

.. figure:: ./images/exercise02-11.png
   :width: 80%

変更を反映する前に、Config Client上のプロパティを確認しましょう。

.. code-block:: bash

    $ curl -X GET http://localhost:8081/env/urlshorten.url
    http://localhost:8081


次にConfig Clientをrefreshします。

.. code-block:: bash

    $ curl -X POST http://localhost:8081/refresh
    ["urlshorten.url"]
    $ curl -X GET http://localhost:8081/env/urlshorten.url
    http://localhost:9999

変更が反映されました。しかし、以下の通りDI済みのプロパティに再DIはされていません。

.. code-block:: bash

    $ curl -X POST http://localhost:8081 -d "url=http://google.com"
    http://localhost:8081/58f3ae21

今度はConfig Clientをrestartします。

.. code-block:: bash

    $ curl -X POST http://localhost:8081/restart
    {"message":"Restarting"}

restart後は、最新のプロパティで再DIされていることがわかります。

.. code-block:: bash

    $ curl -X POST http://localhost:8081 -d "url=http://google.com"
    http://localhost:9999/58f3ae21


課題3 「URL短縮サービス」(Config Client)をRefreshスコープに変更
--------------------------------------------------------------------------------
「URL短縮サービス(\ ``UrlShortener``\ クラス)」へのプロパティインジェクション反映をrefreshで行えるように、
\ ``UrlShortener``\ クラスをRefreshスコープに変更してください。

.. code-block:: java

    @EnableAutoConfiguration
    @ComponentScan
    @RestController
    @RefreshScope // ここを追加
    public class UrlShortener {
        // 略
    }

\ ``mvn spring-boot:run``\ で起動した「URL短縮サービス」をCtrl+Cで終了して、再度実行してください。


.. code-block:: bash

    $ mvn spring-boot:run -f urlshortener/pom.xml -Drun.arguments="--server.port=8081"

今回は以下のようにEnvエンドポイントにPOSTすることでコンフィギュレーションを変更しましょう。


.. code-block:: bash

    $ curl -X POST http://localhost:8081/env -d urlshorten.url=http://127.0.0.1:9999
    {"urlshorten.url":"http://127.0.0.1:9999"}

再度、refreshを行い、もう一度「URL短縮サービス」へリクエストを送りましょう。

.. code-block:: bash

    $ curl -X POST http://localhost:8081/refresh
    []
    $ curl -X POST http://localhost:8081 -d "url=http://google.com"
    http://127.0.0.1:9999/58f3ae21

restartすることなく、アプリケーションにプロパティが反映されたことがわかります。

Config Server、Config ClientともにCtrl+Cで終了してください。(Gitbucket, Redisは起動したままにしてください。）

演習3 Spring Cloud Netflixでマイクロサービスアーキテクチャ構築
================================================================================
演習3ではSpring Cloud Netflixを使った様々なパターンを体験します。


演習3で扱うシステムのアーキテクチャ図を以下に示します。

.. figure:: ./images/exercise03-01.png
   :width: 80%
