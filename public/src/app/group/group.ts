/**
 * Group Model class
 */
export class Group {

  id: string;
  name: string;
  email: string;
  description: string;
  checked: boolean = false;

  constructor(id: string, name: string, email: string, description: string) {
    this.id  = id;
    this.name = name;
    this.email = email;
    this.description = description;
  }

  json() {
    return {
      id: this.id,
      name: this.name,
      email: this.email,
      desc: this.description
    }
  }

  static fromJson(json: any): Group {
    return new Group(json.id, json.name, json.email, json.description)
  }

}
