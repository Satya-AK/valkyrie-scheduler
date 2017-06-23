

import {Observable} from 'rxjs/Rx';

export class BaseApiService {

  protected handleError (error: Response | any) {
    // In a real world app, you might use a remote logging infrastructure
    let errMsg: string;
    if (error.constructor.name == "Response") {
      const body: any = error.json() || '';
      const err = body.error_message || JSON.stringify(body);
      errMsg =  `${err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    return Observable.throw(errMsg);
  }

}
