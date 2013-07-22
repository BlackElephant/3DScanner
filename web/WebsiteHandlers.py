

import MainHandler
import re
import copy

import os
import logging
import urllib2
import json
import urllib
import math
from datetime import tzinfo, timedelta, datetime

from google.appengine.api import memcache
from google.appengine.api import mail

f = open('content.json', 'r')
content = json.loads(f.read())

class GeneralHandler(MainHandler.Handler):
    def get(self):
        global content

        self.render("index.html",content=content)
    
    def post(self):
        pass

class ViewHandler(MainHandler.Handler):
    def get(self,id):
        global content
        
        if id:
            for model in content:
                if int(model['id']) == int(id):
                    self.render("model.html",model=model['points'])
    
    def post(self,id):
        global content
        
        id = 0
        for model in content:
            if id < int(model['id']):
                id = int(model['id']) + 1

        post_str = json.loads(self.request.body)
        distances = post_str['distances']
        heights = post_str['heights']
        rotation = post_str['rotations']
                    
        points = []
        for i in xrange(len(distances)):
            point = []
            point.append(distances[i]*math.cos(math.radians(rotation[i]))/12.0)
            point.append(distances[i]*math.sin(math.radians(rotation[i]))/12.0)
            point.append(heights[i]*(1.0/5.0))
            points.append(point)
                 
        self.response.out.write(points)
        #self.response.out.write("\n\n\n")
        dict = { 'id' : id, 'date' : "Test!", 'points' : points}
        content.append(dict)
        #self.response.out.write(content)


class ModelHandler(MainHandler.Handler):
    def get(self,id):
        global content
        
        if id:
            for model in content:
                if int(model['id']) == int(id):
                    self.response.headers["Content-Type"] = "application/octet-stream"
                    self.response.out.write(model['points'])
    
    def post(self):
        pass

