「2週間でできる! スクリプト言語の作り方 」(千葉 滋) で題材になっているstone言語を実装したものです。

cloneしたディレクトリをeclipseで開いてください。src/runners/EvalRunner.javaにmain関数があります。

関数を実装したmaster、クラスを実装したclass-master、頻繁に実行される関数をllvmを使用しネイティブコンパイルし高速に実行するllvmbackend-maser、の三つのブランチがあります。

この実装ではgluonjを使用せずgitのブランチを切ることで代用しています。
千葉先生の本ではgluonjを使用してモンキーパッチを当てる形で言語仕様と言語処理系を拡張しています。