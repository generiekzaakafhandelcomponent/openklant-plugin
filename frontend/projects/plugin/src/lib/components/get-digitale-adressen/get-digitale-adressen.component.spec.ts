import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GetDigitaleAdressenComponent} from './get-digitale-adressen.component';
import {EMPTY, of} from "rxjs";
import { GetDigitaleAdressenConfig } from "../../models/get-digitale-adressen-config";

describe('GetDigitaleAdressenComponent', () => {
  let component: GetDigitaleAdressenComponent;
  let fixture: ComponentFixture<GetDigitaleAdressenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GetDigitaleAdressenComponent]
    })
      .overrideTemplate(GetDigitaleAdressenComponent, "")
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GetDigitaleAdressenComponent);
    component = fixture.componentInstance;

    component.save$ = EMPTY;
    component.disabled$ = of(false);
    component.pluginId = "test-plugin";

    component.prefillConfiguration$ = of({
      resultPvName: "",
      queryParams: []
    } as GetDigitaleAdressenConfig);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
