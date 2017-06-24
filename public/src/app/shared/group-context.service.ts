import { Injectable } from '@angular/core';


@Injectable()
export class GroupContextService {

  private data: any = {group: "test"};


  constructor() {}

  showModal() {

  }

  setGroup(groupName: string) {
    this.data['group'] = groupName;
  }

  defGroup() {
    return this.data.group;
  }


}
