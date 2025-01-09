# The Not-So-Excel-lent Spreadsheet 📊

A Java implementation of a spreadsheet that's trying its best to be Excel when it grows up. It's not quite there yet, but it's doing its homework and eating its vegetables.

## Features 🌟

- Basic arithmetic operations (+, -, *, /) because we believe in starting with the fundamentals
- Cell references (A1, B2, etc.) that actually work most of the time
- Formula evaluation that won't make your math teacher cry
- Circular dependency detection to prevent your spreadsheet from chasing its own tail
- A GUI that's minimalist (we call it "retro")

## Getting Started 🚀

1. Clone this repository (or just copy-paste it, we won't judge)
2. Run `mvn test` to make sure nothing's broken
3. Start the GUI and pretend you're using Excel 95

## Usage Examples 💡

```java
sheet.set(0, 0, "42");          // The answer to everything
sheet.set(1, 0, "=A0 + 1");     // The answer to everything plus one
sheet.set(2, 0, "=B0 * 2");     // Now we're just showing off
```
## The Great IDE Migration 🏃
After a dramatic breakup with IntelliJ (JUnit kept ghosting me), I found true happiness with VS Code + Maven. It's like when you finally switch from writing formulas on a napkin to using an actual calculator. Now my tests run smoother than a spreadsheet's bottom row.

## Known Features That Are Actually Bugs 🐛
Sometimes cells play hide and seek with their values
The GUI occasionally takes coffee breaks without warning
If you type too fast, the spreadsheet needs a moment to catch up (it's not lazy, just thoughtful)
## Contributing 🤝
PRs welcome! Just make sure your code passes the tests (they actually work now, thanks Maven!) and doesn't make the spreadsheet cry.

## License 📜
Licensed under the "It Works On My Machine™" public license. Made with ❤️ and ☕ for Ariel University's Java Course