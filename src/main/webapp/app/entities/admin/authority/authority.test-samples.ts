import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '528ec20d-38a7-4dc1-8ef4-816b28558ddd',
};

export const sampleWithPartialData: IAuthority = {
  name: '41a40180-ef35-42a1-b8d3-4f9f915059a1',
};

export const sampleWithFullData: IAuthority = {
  name: 'b45f0cdb-bfe5-4364-b828-fd13a2fb8479',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
