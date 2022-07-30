
class Node(object):
    '''
    classdocs
    '''


    def __init__(self, value, room, estimate, prev, left=None, right=None):
        '''
        Constructor
        '''
        self.value = value
        self.room = room
        self.estimate = estimate        
        self.prev = prev
        self.left = left
        self.right = right
        
    def __eq__(self, other):
        return object.__eq__(self, other)
    
    def __ne__(self, other):
        return object.__ne__(self, other)
    
    def __lt__(self, other):
        return object.__lt__(self, other)
    
    def __gt__(self, other):
        return object.__gt__(self, other)
    
    def __le__(self, other):
        return object.__le__(self, other)
    
    def __ge__(self, other):
        return object.__ge__(self, other)
    
    
        
        