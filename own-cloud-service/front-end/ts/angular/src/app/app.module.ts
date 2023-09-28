import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginPageComponent } from './template/page/login-page/login-page.component';
import { InputComponent } from './template/component/input/input.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginPageComponent,
    InputComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
