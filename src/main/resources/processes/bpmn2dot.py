import sys
import pm4py
from pm4py.visualization.bpmn import visualizer as bpmn_visualizer

def convert_bpmn_to_dot(input_file, output_file):
    # 1. Read the BPMN file
    bpmn_graph = pm4py.read_bpmn(input_file)
    
    # 2. Convert to Graphviz object
    # The 'apply' function returns a graphviz.Digraph object
    gviz = bpmn_visualizer.apply(bpmn_graph)
    
    # 3. Save the DOT source code
    # .source contains the raw DOT syntax
    with open(output_file, "w") as f:
        f.write(gviz.source)
    
    print(f"Success! DOT file saved to: {output_file}")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python bpmn2dot.py <input.bpmn> <output.dot>")
    else:
        convert_bpmn_to_dot(sys.argv[1], sys.argv[2])

