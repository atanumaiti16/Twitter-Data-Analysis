from tweepy import Stream
from tweepy import OAuthHandler
from tweepy.streaming import StreamListener
import time
import json
import urllib

ckey = 'oXdbNFctfSb7sRINv0aWJdXs0'
csecret = 'ejYi3aYglTe4lKdL88hPv6VLkEQPS83O4ffku0sCPjOJ44mMYj'
atoken = '1633855369-g9F2wjzH2k2V0kIDkCw13fKinQ1l7u3ex0zw6NJ'
asecret = 'E5FiQAWDHiR4nSDvJnB4TGRUAOTDElfsUNkRov5laCobT'


class listener(StreamListener):
    def on_data(self, data):

            print data
            saveFile = open('tweetPhase2DB.json', 'a')

            saveFile.write(data)
            saveFile.close()   
            return True


    def on_error(self, status):
        print status


auth = OAuthHandler(ckey, csecret)
auth.set_access_token(atoken, asecret)
twitterStream = Stream(auth, listener())
twitterStream.filter(track=["election"])


