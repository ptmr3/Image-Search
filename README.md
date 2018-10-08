# ImageSearch

------

#### ImageSearch allows you to search and download images from https://unsplash.com/.



<a href="url"><img src="https://github.com/ptmr3/image-search/blob/master/doc/Screenshot_1.png" width="155" ></a>
<a href="url"><img src="https://github.com/ptmr3/image-search/blob/master/doc/Screenshot_2.png" width="155" ></a>
<a href="url"><img src="https://github.com/ptmr3/image-search/blob/master/doc/Screenshot_3.png" width="155" ></a>
<a href="url"><img src="https://github.com/ptmr3/image-search/blob/master/doc/Screenshot_4.png" width="155" ></a>
<a href="url"><img src="https://github.com/ptmr3/image-search/blob/master/doc/Screenshot_5.png" width="155" ></a>


#### Composition and Architecture

--  This application was build using no 3rd party libraries except for basic
testing libraries that are only while running unit tests.

--  The Flux architectural style was used to build this app to ensure
  separation of concerns, as well as an extendable single directional workflow.
  The architectural style was implemented using the Observable/Observer classes provided in Java.


####   Let's get more specific

![ImageSearchArch](https://github.com/ptmr3/image-search/blob/master/doc/ImageSearchArch.png)



The Fluxx flow consists of 3 major layers: the View Layer, the Action Layer, and the Store Layer

- **View Layer** - The View Layer only handles what it must. For the most part, this entails
handling of UI components, and initiating action flows.
- **Action Layer** - The Action Layer is solely responsible for publishing
actions and data for the Store Layer to act upon.
- **Store Layer** -  The Store Layer is the holder of all logic and state.
Once an action is processed and all associated logic is applied, Reactions
are published to the View Layer to kick of any reactionary UI work.
