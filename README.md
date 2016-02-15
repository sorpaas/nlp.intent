# nlp.intent

**nlp.intent** ([Project Page](https://source.id.hn/diffusion/NLPI/),
[Github Mirror](https://github.com/sorpaas/nlp.intent)) is an open source
[wit.ai](https://wit.ai) alternative for natural language processing. It can be
used to recognize **intent** and **slots**. For example, in a meeting schedule
application, "*Can you help schedule a 30 minute slot for us tomorrow?*" can be
regarded as a sentence with intent *schedule meeting*, a duration slot *30
minute* and a time slot *tomorrow*.

The project is written in Clojure. It is in its very early stage. Things work,
but all are subject to change. [Submit an issue](https://source.id.hn/maniphest/task/edit/form/default/?projects=nlp.intent).

## Usage

In *Leiningen* or *Boot*, add *nlp.intent* as a dependency:

~~~clojure
[nlp.intent "0.0.1-SNAPSHOT"]
~~~

The thing for classifying intent and slots is called "rater". Create a rater:

~~~clojure
(require '[nlp.intent :as intent])
(def my-rater (intent/new-rater))
~~~

Add a new type to the rater. A type is a Clojure vector where its first element
is a name, and the rest of its elements are slot types. According to
[CoreNLP](http://stanfordnlp.github.io/CoreNLP/) (which nlp.intent used under
the hood), supported types are `:money`, `:time`, `:date`, `:percent`,
`:number`, `:ordinal`, `:duration`, `:person`, `:organization`, and `:location`.

~~~clojure
(intent/add-type my-rater [:schedule-meeting :duration])
~~~

Then we can train the rater. Note that you need at least 6 samples to make the
rater return any result.

~~~clojure
(intent/train my-rater :schedule-meeting "Can you schedule a 30 minute slot for us tomorrow?")
(intent/train my-rater :schedule-meeting "Please help schedule a 45 minute time for us on Wednesday.")
(intent/train my-rater :schedule-meeting "Can you help schedule a 1 hour slot for us the
                                          day after tomorrow?")
(intent/train my-rater :schedule-meeting "Would you please schedule a 30 minute slot for us tomorrow?")
(intent/train my-rater :schedule-meeting "Schedule a 30 minute slot for us tomorrow, please")
(intent/train my-rater :schedule-meeting "Hey, please schedule a 30 minute slot for us tomorrow")
(intent/train my-rater :schedule-meeting "Would you mind scheduling a 30 minute slot for us tomorrow?")
~~~

After training, you must `sync` to update the internal classifier. You can
`train` and `sync` a rater multiple times.

~~~clojure
(intent/sync my-rater)
~~~

And finally we can classify a sentence. The function `rate` returns a vector
where its first element is the classified type name (*the intent*), and its rest
of elements are slot instances (strings) which order is the same as when you
define the type using `add-type`. If a slot is not found in a sentence, that
place will be set to `nil`.

~~~clojure
(intent/rate my-rater "Could you help schedule a 30 minutes slot for us on Thursday?")
; => [:schedule-meeting "30 minutes"]
~~~

That's it. Have fun building your own customized [meeting](https://x.ai/)
[scheduler](https://geekbot.io/) and lunch organizer!

## Under the Hood

nlp.intent uses [CoreNLP](http://stanfordnlp.github.io/CoreNLP/)'s Named Entity
Recognition for recognizing slots, and [OpenNLP](https://opennlp.apache.org/)'s
Doccat model for recognizing intent.

## Limitation

For now, nlp.intent is in its very early stage, so it may not be as accurate as
you want it to be. Each slot types can only be used once in a rater type. So
only `[:schedule-meeting :duration :date :time]` would work, but not
`[:schedule-meeting :duration :duration :date :time]`.

## Examples

A
[slackbot example](https://source.id.hn/diffusion/NLPI/browse/master/examples/slackbot.clj)
can be found in the `examples` folder.

## Contribution

The project is managed in a
[Phabricator instance](https://source.id.hn/diffusion/NLPI/). For issues, submit
one
[here](https://source.id.hn/maniphest/task/edit/form/default/?projects=nlp.intent).
For patches, use
[arc diff](https://secure.phabricator.com/book/phabricator/article/arcanist_diff/)
to submit revisions.

## License

Copyright © 2016 Wei Tang

Distributed under the GNU General Public License either version 3.0 or (at your
option) any later version.
