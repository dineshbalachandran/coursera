'''
Created on 6May,2017

@author: DINESHKB
'''

from knapsack.Node import Node

class bfNode(Node):
    '''
    classdocs
    '''


    def __init__(self, value, room, estimate, depth, filtter, prev, left=None, right=None):
        '''
        Constructor
        '''
        
        Node.__init__(self, value, room, estimate, prev, left, right)
        self.depth = depth
        self.filtter = list(filtter)
        
    def __eq__(self, other):
        return (self.estimate, self.depth) == (other.estimate, other.depth)
    
    def __ne__(self, other):
        return (self.estimate, self.depth) != (other.estimate, other.depth)
    
    def __lt__(self, other):
        return (self.estimate, self.depth) > (other.estimate, other.depth)
    
    def __gt__(self, other):
        return (self.estimate, self.depth) < (other.estimate, other.depth)
    
    def __le__(self, other):
        return (self.estimate, self.depth) >= (other.estimate, other.depth)
    
    def __ge__(self, other):
        return (self.estimate, self.depth) <= (other.estimate, other.depth)
    