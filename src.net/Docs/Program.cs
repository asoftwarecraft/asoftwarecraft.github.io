if (args.Length != 2 || args[0] != "scribble") {
  Console.WriteLine("Usage: Docs scribble input-file");
  return 1;
}
foreach (var line in Scribble.Generate.From(File.ReadLines(args[1]))) {
  Console.WriteLine(line);
}
return 0;