#!/usr/bin/env python3
"""Static checks for the Stonecutter comment syntax used in src/main/java.

Stonecutter rewrites the sources for every target version by keeping one branch
of each ``//? if ... { ... //?} else { ... //?}`` block and commenting the other
one out. Two mistakes break that rewrite in ways javac reports very indirectly:

1. An unbalanced block: a ``//?} else`` that is never closed with ``//?}`` gets
   paired with the next directive further down the file and swallows real code.
2. A nested block comment: the inactive branch is stored as a ``/* ... */``
   comment, so any ``/*`` or ``*/`` inside it (a Javadoc, for example) closes it
   early and leaves the rest of the branch as live code.

Both produced "class, interface, enum, or record expected" errors at the end of
FirstPersonPlayerRenderer.java before they were fixed.

Usage: python3 scripts/check_stonecutter_comments.py [path ...]
"""

from __future__ import annotations

import pathlib
import re
import sys

DIRECTIVE = re.compile(r"//\?\s*(.*)$")


def strip_comment_markers(text: str) -> str:
    return text.replace("*/", "").replace("/*", "").strip()


def check_blocks(path: pathlib.Path, lines: list[str]) -> list[str]:
    """Every ``//? if ... {`` must be closed by a matching ``//?}``."""
    problems: list[str] = []
    stack: list[int] = []
    for number, raw in enumerate(lines, 1):
        match = DIRECTIVE.search(raw)
        if match is None:
            continue
        directive = strip_comment_markers(match.group(1).strip())
        if directive.startswith("}"):
            rest = directive[1:].strip()
            if not stack:
                problems.append(f"{path}:{number}: '//}}' without a matching '//? if ... {{'")
                continue
            stack.pop()
            if rest == "":
                continue
            if rest.startswith("else"):
                if rest.endswith("{"):
                    stack.append(number)
                else:
                    problems.append(f"{path}:{number}: '//?}} else' without a '{{' - the branch "
                                    "is never closed and swallows the code that follows")
                continue
            problems.append(f"{path}:{number}: unexpected text after '//}}': {directive!r}")
        elif directive.startswith(("if", "else if")) and directive.endswith("{"):
            stack.append(number)
    for number in stack:
        problems.append(f"{path}:{number}: '//? if' block is never closed with '//?}}'")
    return problems


def check_comments(path: pathlib.Path, text: str) -> list[str]:
    """Block comments must not nest and must not be left open."""
    problems: list[str] = []
    index, length = 0, len(text)
    in_block = in_line = False
    line_number = 1
    while index < length:
        char = text[index]
        following = text[index + 1] if index + 1 < length else ""
        if char == "\n":
            line_number += 1
            in_line = False
            index += 1
            continue
        if in_line:
            index += 1
            continue
        if in_block:
            if char == "*" and following == "/":
                in_block = False
                index += 2
                continue
            if char == "/" and following == "*":
                problems.append(f"{path}:{line_number}: nested '/*' inside a block comment "
                                "(breaks the commented-out Stonecutter branch)")
                index += 2
                continue
            index += 1
            continue
        if char == "/" and following == "/":
            in_line = True
            index += 2
            continue
        if char == "/" and following == "*":
            in_block = True
            index += 2
            continue
        if char == "*" and following == "/":
            problems.append(f"{path}:{line_number}: stray '*/' outside a block comment")
            index += 2
            continue
        if char in "\"'":
            quote = char
            index += 1
            while index < length and text[index] != quote:
                if text[index] == "\\":
                    index += 1
                index += 1
            index += 1
            continue
        index += 1
    if in_block:
        problems.append(f"{path}:{line_number}: unterminated block comment at end of file")
    return problems


def main(argv: list[str]) -> int:
    roots = [pathlib.Path(a) for a in argv] or [pathlib.Path("src"), pathlib.Path("fabric"),
                                                pathlib.Path("neoforge"), pathlib.Path("forge")]
    problems: list[str] = []
    checked = 0
    for root in roots:
        files = [root] if root.is_file() else sorted(root.rglob("*.java"))
        for path in files:
            if ".git" in path.parts:
                continue
            text = path.read_text(errors="replace")
            checked += 1
            problems += check_blocks(path, text.split("\n"))
            problems += check_comments(path, text)
    for problem in problems:
        print(problem)
    print(f"checked {checked} java file(s), {len(problems)} problem(s)")
    return 1 if problems else 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
