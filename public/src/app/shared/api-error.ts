

export interface APIResponse<T> {
   data: T
}

/**
 * API success data
 */
export class APISuccess<T> implements APIResponse<T> {

  constructor(public data: T) {}

}



/**
 *  API Error class
 */
export class ApiError<T> implements APIResponse<T> {

  data: T = null;

 constructor(public errorMessage: string) {}

}
