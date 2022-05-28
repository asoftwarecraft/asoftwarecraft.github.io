using NUnit.Framework;

namespace Scribble;

public static class Line {
  public static LineType Type(string content) {
    return content.All(char.IsWhiteSpace) ? LineType.Blank
      : content.StartsWith("//@") ? LineType.Scribble
      : content.StartsWith("//") ? LineType.Comment
        : LineType.Code;
  }
}

internal class LineTest {
  [TestCase(LineType.Blank, "")]
  [TestCase(LineType.Blank, " \t\r\n")]
  [TestCase(LineType.Scribble, "//@a")]
  [TestCase(LineType.Comment, "// a")]
  [TestCase(LineType.Code, " T\r\n")]
  [TestCase(LineType.Code, " // a")]
  public void IdentifyLineType(LineType expected, string content) {
    Assert.AreEqual(expected, Line.Type(content));
  }
}