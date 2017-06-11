Examples
========

CJK line-wrap example
---------------------

Sub-set of options of :manpage:`ls(1)`. The help messages are from
Debian Linux::

    import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;
    import net.sourceforge.argparse4j.ArgumentParsers;
    import net.sourceforge.argparse4j.inf.ArgumentParser;
    import net.sourceforge.argparse4j.inf.ArgumentParserException;
    import net.sourceforge.argparse4j.inf.Namespace;

    public class LsHelpDemo {

	public static void main(String[] args) {
	    ArgumentParser parser = ArgumentParsers
            .newFor("ls")
            .build()
		    .defaultHelp(true)
		    .description(
			    "FILE に関する情報を一覧表示します (デフォルトは現在のディレクトリ)。\n"
				    + "-cftuvSUX または --sort が指定されない限り、要素はアルファベット順で並べ替えられます。")
		    .epilog("SIZE は次のうちの一つです (整数の後に付加されるかもしれません):\n"
			    + "KB 1000, K 1024, MB 1000*1000, M 1024*1024, その他 G, T, P, E, Z, Y など。");
	    parser.addArgument("-a", "--all").help(". で始まる要素を無視しない")
		    .action(storeTrue());
	    parser.addArgument("-A", "--almost-all").help(". および .. を一覧表示しない")
		    .action(storeTrue());
	    parser.addArgument("--author").help("-l と合わせて使用した時、各ファイルの作成者を表示する")
		    .action(storeTrue());
	    parser.addArgument("-b", "--escape")
		    .help("表示不可能な文字の場合に C 形式のエスケープ文字を表示する").action(storeTrue());
	    parser.addArgument("--block-size")
		    .type(Integer.class)
		    .metavar("SIZE")
		    .help("SIZE の倍数として表示する。例: `--block-size=M' は"
			    + "表示する時に 1,048,576 バイトの倍数としてサイズを"
			    + "表示する。SIZE の形式は以下を参照)");
	    parser.addArgument("-B", "--ignore-backups").help("~ で終了する要素を一覧表示しない")
		    .action(storeTrue());
	    parser.addArgument("-c")
		    .help("-lt と使用した場合: ctime (ファイル状態情報を変更した時間)で並べ替えて表示する\n"
			    + "-l と使用した場合: 名前で並べ替えて ctime を表示する\n"
			    + "それ以外: ctime で新しい順に並べ替える)").action(storeTrue());
	    parser.addArgument("-C").help("要素を列ごとに並べる").action(storeTrue());
	    parser.addArgument("--color")
		    .nargs("?")
		    .choices("always", "never", "auto")
		    .metavar("WHEN")
		    .setDefault("always")
		    .help("カラー出力をする。 WHEN のデフォルト値は `always'、"
			    + "`never' または `auto'。詳細は下部を参照)");
	    parser.addArgument("file").nargs("*");
	    Namespace res;
	    try {
		res = parser.parseArgs(args);
		System.out.println(res);
	    } catch (ArgumentParserException e) {
		parser.handleError(e);
		System.exit(1);
	    }
	}
    }

.. code-block:: console

    $ java LsHelpDemo -h
    usage: ls [-h] [-a] [-A] [--author] [-b] [--block-size SIZE] [-B] [-c] [-C]
	      [--color [WHEN]] [file [file ...]]

    FILE に関する情報を一覧表示します (デフォルトは現在のディレクトリ)。
    -cftuvSUX または  --sort が指定されない限り、要素はアルファベット順で並べ替
    えられます。

    positional arguments:
      file

    named arguments:
      -h, --help             show this help message and exit
      -a, --all              . で始まる要素を無視しない (default: false)
      -A, --almost-all       . および .. を一覧表示しない (default: false)
      --author               -l  と合わせて使用した時、各ファイルの作成者を表示
			     する (default: false)
      -b, --escape           表示不可能な文字の場合に C  形式のエスケープ文字を
			     表示する (default: false)
      --block-size SIZE      SIZE の倍数として表示する。例: `--block-size=M' は
			     表示する時に 1,048,576  バイトの倍数としてサイズを
			     表示する。SIZE の形式は以下を参照)
      -B, --ignore-backups   ~ で終了する要素を一覧表示しない (default: false)
      -c                     -lt と使用した場合:  ctime (ファイル状態情報を変更
			     した時間)で並べ替えて表示する
			     -l と使用した場合: 名前で並べ替えて ctime を表示す
			     る
			     それ以外: ctime  で新しい順に並べ替える) (default:
			     false)
      -C                     要素を列ごとに並べる (default: false)
      --color [WHEN]         カラー出力をする。      WHEN      のデフォルト値は
			     `always'、`never'  または   `auto'。詳細は下部を参
			     照) (default: always)

    SIZE は次のうちの一つです (整数の後に付加されるかもしれません):
    KB 1000, K 1024, MB 1000*1000, M 1024*1024, その他 G, T, P, E, Z, Y など。



Clojure example
---------------

.. code-block:: clojure

    (ns argparse4j-demo
	(:import (net.sourceforge.argparse4j ArgumentParsers)
		 (net.sourceforge.argparse4j.impl Arguments)
		 (net.sourceforge.argparse4j.inf ArgumentParserException)))

    ;; Helper function to convert closure list to array
    (defn va [& name-and-flags]
      (into-array name-and-flags))

    (def apb (. ArgumentParsers newFor
           "java -cp clojure.jar clojure.main"))
    (def ap (. apb build))

    (let [group (. ap addArgumentGroup "init options")]
	 (doto (. group addArgument (va "-i" "--init"))
	       (.metavar (va "path"))
	       (.help "Load a file or resource"))
	 (doto (. group addArgument (va "-e" "--eval"))
	       (.metavar (va "string"))
	       (.help "Evaluate expressions in string; print non-nil values")))

    (let [group (. ap addArgumentGroup "main options")]
	 (doto (. group addArgument (va "-m" "--main"))
	       (.metavar (va "ns-name"))
	       (.help "Call the -main function from a namespace with args"))
	 (doto (. group addArgument (va "-r" "--repl"))
	       (.action (. Arguments storeTrue))
	       (.help "Run a repl"))
	 (doto (. group addArgument (va "path"))
	       (.help (str "Run a script from a file or resource;"
			   " use '-' to read from standard input"))))

    (try
     (println (. ap parseArgs (into-array String *command-line-args*)))
     (catch RuntimeException e
	    (if (instance? ArgumentParserException (. e getCause))
		(. ap handleError (. e getCause))
	      (. e printStackTrace))
	    (. System exit 1)))

With Closure, use :javatype:`Long` type instead of :javatype:`Integer`
if you use |Argument.choices| with integer constants:

.. code-block:: clojure

    (def apb (. ArgumentParsers newFor "hello"))
    (def ap (. apb build))
    (doto (. ap addArgument (va "-i"))
	  (.type Long)
	  (.choices [1 2 3])
	  (.action (. Arguments append)))

.. |Argument.choices| replace:: :javadocfunc:`inf.Argument.choices(E...)`
