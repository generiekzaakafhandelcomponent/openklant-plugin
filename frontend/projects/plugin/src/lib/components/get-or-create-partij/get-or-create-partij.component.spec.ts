import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GetOrCreatePartijComponent} from './get-or-create-partij.component';
import {EMPTY, of} from "rxjs";
import {GetOrCreatePartijConfig} from "../../models/get-or-create-partij-config";

describe('GetOrCreatePartijComponent', () => {
  let component: GetOrCreatePartijComponent;
  let fixture: ComponentFixture<GetOrCreatePartijComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GetOrCreatePartijComponent]
    })
      .overrideTemplate(GetOrCreatePartijComponent, "")
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GetOrCreatePartijComponent);
    component = fixture.componentInstance;

    component.save$ = EMPTY;
    component.disabled$ = of(false);
    component.pluginId = "test-plugin";

    component.prefillConfiguration$ = of({
      bsn: "",
      voorletters: "",
      voornaam: "",
      voorvoegselAchternaam: "",
      achternaam: "",
    } as GetOrCreatePartijConfig);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  function validityOf(config: GetOrCreatePartijConfig): boolean[] {
    const emitted: boolean[] = [];
    component.valid.subscribe(valid => emitted.push(valid));
    component.formValueChange(config);
    return emitted;
  }

  it('should accept a BSN without name fields', () => {
    expect(validityOf({bsn: 'pv:bsn'})).toEqual([true]);
  });

  it('should accept a KVK-nummer', () => {
    expect(validityOf({kvkNummer: 'pv:kvkNummer', organisatienaam: 'pv:bedrijfsnaam'})).toEqual([true]);
  });

  it('should accept both, as each may point to a process variable', () => {
    expect(validityOf({bsn: 'pv:bsn', kvkNummer: 'pv:kvkNummer'})).toEqual([true]);
  });

  it('should reject a configuration without BSN and KVK-nummer', () => {
    expect(validityOf({voornaam: 'Jan'})).toEqual([false]);
  });
});
