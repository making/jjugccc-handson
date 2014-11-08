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

演習1 Spring Bootで「URL短縮サービス」を作る
================================================================================

演習1ではマイクロサービス界でのFizzBuzz問題である、「URL短縮サービス」を作ります。

課題1で\ ``ConcurrentHashMap``\ を使った実装。課題2でRedisを使った実装を行います。

インポートするプロジェクトにほとんどのコードが実装されているので、課題で実装するコードはほんの数行です。

プロジェクトの作成・インポート
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


JDKが未設定の場合は、「New」を押してJDKを設定してください。JAVA_HOMEを選択すれば良いです。


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

演習2 Spring Cloud Configで動的コンフィギュレーション
================================================================================




演習3 Spring Cloud Netflixでマイクロサービスアーキテクチャ構築
================================================================================