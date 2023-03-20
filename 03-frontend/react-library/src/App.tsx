import React from 'react';
import { Redirect, Route, Switch } from 'react-router-dom';
import './App.css';
import { HomePage } from './layouts/HomePage/HomePage';
import { Footer } from './layouts/NavbarAndFooter/Footer';
import { Navbar } from './layouts/NavbarAndFooter/Navbar';
import { SearchBooksPage } from './layouts/SearchBooksPage/SearchBooksPage';

export const App = () => {
  return (
    <div className='d-flex flex-column min-vh-100 '>
      <Navbar />
      <div className='flex-grow-1'> {/** This div is to keep footer at the bottom of the page even if there is no content in the webpage */}
        <Switch> {/** Switch allows us to render only one route i.e. only homepage will get rendered when /search is given because of / in the front and it refers to homepage. This can be overcome with exact keyword */}
          <Route path='/' exact>
            <Redirect to='/home' />
          </Route>
          <Route path='/home'>
            <HomePage />
          </Route>
          <Route path='/search'>
            <SearchBooksPage />
          </Route>
        </Switch>
      </div>
      <Footer />
    </div>
  );
}
