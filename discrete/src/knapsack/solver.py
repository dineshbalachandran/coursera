#!/usr/bin/python
# -*- coding: utf-8 -*-

from collections import namedtuple
from knapsack.Node import Node
from knapsack.bfNode import bfNode
from queue import PriorityQueue

Item = namedtuple("Item", ['index', 'value', 'weight'])
WItem = namedtuple("WItem", ['v_by_w', 'item'])

def trivial_greedy(items, capacity):
    # a trivial greedy algorithm for filling the knapsack
    # it takes items in-order until the knapsack is full
    # the solution is non-optimal
    
    value = 0
    weight = 0
    taken = [0]*len(items)

    for item in items:
        if weight + item.weight <= capacity:
            taken[item.index] = 1
            value += item.value
            weight += item.weight

    return (value, taken)


def weighted_greedy(items, capacity):
    # a weighted greedy algorithm for filling the knapsack
    # it takes items in-order of value/weight until the knapsack is full
    # the solution is non-optimal
    
    value = 0
    weight = 0
    taken = [0]*len(items)
    
    witems = sorted([WItem(item.value/item.weight, item) for item in items], reverse=True)
    
    for item in witems:
        if weight + item.item.weight <= capacity:
            taken[item.item.index] = 1
            value += item.item.value
            weight += item.item.weight
            
    return (value, taken)


def dp(items, capacity):
    # a dynamic programming algorithm for filling the knapsack
    # until the knapsack is full, the solution is optimal though not scalable
    # It is pseudo-polynomial for small values of n and exponential otherwise
    
    n = len(items)
    
    # capacity (k) in row and items (i) in column
    table = [[0 for i in range(0, n + 1)] for k in range(0, capacity + 1)]
    
    for i in range(1, n + 1):       
        for k in range(1, capacity + 1):
            if items[i-1].weight > k:                           
                table[k][i] = table[k][i-1]
            else:
                weight = items[i-1].weight
                table[k][i] = max(items[i-1].value + table[k-weight][i-1], table[k][i-1])
    
    value = table[capacity][n]
    
    # trace back to get the items that form the optimal solution
    taken = [0]*n
    
    k = capacity
    for i in range(n, 0, -1):
        if table[k][i-1] != table[k][i]:
            taken[i-1] = 1
            k -= items[i-1].weight 
        
    
    return (value, taken)


def df(items, capacity):
    # a depth first branch and bound method for filling the knapsack
    # until the knapsack is full, the solution is optimal
    
    n = len(items)
    
    witems = sorted([WItem(item.value/item.weight, item) for item in items], reverse=True)
    
    filtter = [1]*n
    bound = linear_relaxation_bound(witems, capacity, filtter, 0)
        
    root = Node(0, capacity, bound, None, None, None)
    
    node = dfs_find_optimal_node(None, root, n, capacity, witems, 0, True, filtter)
    node = dfs_find_optimal_node(node, root, n, capacity, witems, 0, False, filtter)
    
    value = node.value
    
    # trace back to get the items that form the optimal solution
    taken = [0]*n
    
    i = n
    while node is not root:
        if is_node_selected(node, node.prev):            
            taken[witems[i-1].item.index] = 1
        node = node.prev
        i -= 1
    
    return (value, taken)


def dfs_find_optimal_node(optimal, current, n, capacity, witems, i, left, filtter):
    
    if i == n:
        optimal = current if optimal is None or current.value > optimal.value else optimal
        return optimal
    
    if left:
        filtter[i] = 1
        if witems[i].item.weight <= current.room:            
            node = Node(current.value + witems[i].item.value, current.room - witems[i].item.weight, current.estimate, current, None, None)
            current.left = node
        else:
            return optimal
    else:
        filtter[i] = 0
        new_estimate = linear_relaxation_bound(witems, capacity, filtter, i)
        if optimal is None or new_estimate > optimal.estimate:
            node = Node(current.value, current.room, new_estimate, current, None, None)
            current.right = node
        else:
            return optimal
            
    optimal = dfs_find_optimal_node(optimal, node, n, capacity, witems, i + 1, True, filtter)
    optimal = dfs_find_optimal_node(optimal, node, n, capacity, witems, i + 1, False, filtter)
            
    return optimal


def is_node_selected(child, parent):
    
    if parent.left is child:
        return True
    else:
        return False
    

def linear_relaxation_bound(witems, capacity, filtter, j):
    # apply linear relaxation, to determine the upper bound of the value
    bound = 0
    weight = 0
    
    for i in range(0, len(witems)):
        
        if i <= j and filtter[i] == 0:
            continue
        
        if weight + witems[i].item.weight <= capacity:            
            bound += witems[i].item.value
            weight += witems[i].item.weight
        else:
            bound += ((capacity - weight) / witems[i].item.weight) * witems[i].item.value
            break
        
    return bound


def bf(items, capacity):
    # a best first branch and bound method for filling the knapsack
    # until the knapsack is full, the solution is optimal
    
    n = len(items)
    
    witems = sorted([WItem(item.value/item.weight, item) for item in items], reverse=True)
    
    filtter = [1]*n
    bound = linear_relaxation_bound(witems, capacity, filtter, 0)
    
    root = bfNode(0, capacity, bound, -1, [], None, None, None)
    
    q = PriorityQueue()
    q.put((-bound, root))
    
    node = q.get()[1]
    optimal = None
    
    while True:
        
        (left, right) = getChildren(node, witems, capacity)
        
        if left is not None:
            q.put((-left.estimate, left))
        
        if right is not None:
            q.put((-right.estimate, right))
        
        node = q.get()[1]
        
        if node.depth == (n - 1):
            optimal = node if optimal is None or node.value > optimal.value else optimal
            
        if optimal is not None and optimal.estimate >= node.estimate:
            node = optimal
            break
    
    
    value = node.value
    
    # trace back to get the items that form the optimal solution
    taken = [0]*n
    
    i = n
    while node is not root:
        if is_node_selected(node, node.prev):            
            taken[witems[i-1].item.index] = 1
        node = node.prev
        i -= 1
    
    return (value, taken)

def getChildren(parent, witems, capacity):
    
    i = parent.depth + 1
    filtter = list(parent.filtter)
    
    #left
    left = None
    filtter.append(1)
    if witems[i].item.weight <= parent.room:            
        left = bfNode(parent.value + witems[i].item.value, parent.room - witems[i].item.weight, parent.estimate, i, filtter, parent, None, None)
    
    #right
    filtter[i] = 0    
    new_estimate = linear_relaxation_bound(witems, capacity, filtter, i)
    right = bfNode(parent.value, parent.room, new_estimate, i, filtter, parent, None, None)
    
    parent.left = left
    parent.right = right
    
    return (left, right)


def solve_it(input_data):
    # Modify this code to run your optimization algorithm

    # parse the input
    lines = input_data.split('\n')

    firstLine = lines[0].split()
    item_count = int(firstLine[0])
    capacity = int(firstLine[1])

    items = []

    for i in range(1, item_count+1):
        line = lines[i]
        parts = line.split()
        items.append(Item(i-1, int(parts[0]), int(parts[1])))

    value = 0
    taken = []
    
    if item_count <= 50:
        (value, taken) = df(items, capacity)
    elif capacity <= 100000:
        (value, taken) = dp(items, capacity)
    else:
        (value, taken) = df(items, capacity)
        
    
    # prepare the solution in the specified output format
    output_data = str(value) + ' ' + str(1) + '\n'
    output_data += ' '.join(map(str, taken))
    return output_data


if __name__ == '__main__':
    import sys
    if len(sys.argv) > 1:
        file_location = sys.argv[1].strip()
        with open(file_location, 'r') as input_data_file:
            input_dt = input_data_file.read()
        print(solve_it(input_dt))
    else:
        print('This test requires an input file.  Please select one from the data directory. (i.e. python solver.py ./data/ks_4_0)')
