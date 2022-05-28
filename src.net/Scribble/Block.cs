using NUnit.Framework;

namespace Scribble; 

public class Block {
  public static IEnumerable<Block> Collect(IEnumerable<string> input) {
    var block = new Block(LineType.Comment);
    foreach (var line in input) {
      block.Append(line.Substring(2));
    }
    yield return block;
  }
  
  public LineType Type { get; }
  public IEnumerable<string> Contents => contents;

  Block(LineType type) {
    Type = type;
  }

  void Append(string input) {
    contents.Add(input);
  }

  readonly List<string> contents = new();
}

internal class BlockTest {
  [Test]
  public void CollectComments() {
    var blocks = Block.Collect(new[] {"//hi"}).ToArray();
    Assert.AreEqual(1, blocks.Length);
    Assert.AreEqual(LineType.Comment, blocks[0].Type);
    Assert.AreEqual("hi", string.Join(';', blocks[0].Contents));
  }
}