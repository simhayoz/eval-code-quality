import javalang
import pprint
import sys

def fileToText(file_path):
    with open(file_path, 'r') as f:
        return f.read()

def main():
    ast = javalang.parse.parse(fileToText(sys.argv[1]))
    # for path, node in ast:
    #     print(path, " \n ", node, "\n")
    pprint.pprint(ast)

if __name__ == "__main__":
    main()
