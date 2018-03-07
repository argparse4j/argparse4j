package net.sourceforge.argparse4j.issue110;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

public class Issue110App {
	public static void main(String[] args) {
		ArgumentParser p = ArgumentParsers.newFor("Issue 110").build();
		p.addArgument("file").type(Arguments.fileType().verifyExists());
		try {
			System.out.println(p.parseArgs(args));
		} catch (ArgumentParserException e) {
			p.handleError(e);
		}
	}
}
